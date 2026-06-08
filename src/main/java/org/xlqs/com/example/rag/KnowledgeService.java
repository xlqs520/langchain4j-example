package org.xlqs.com.example.rag;

import com.google.common.primitives.Floats;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.xlqs.com.example.rag.dto.RagResultDto;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午11:34
 * @description KnowledgeService 类说明：TODO
 * @since 2026/6/7
 */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ai.rag", name = "enabled", havingValue = "true")
public class KnowledgeService {

    public static final int TOP_K = 3;

    public static final float SCORE = 0.7f;

    private final QdrantClient client;

    private final OpenAiEmbeddingModel openAiEmbeddingModel;

    /**
     * 🚀 1. 插入/更新 单条向量数据 (Point)
     *
     * @param collectionName 目标集合名称 (如 "test")
     * @param point          通过 PointBuilder 构建好的单条数据
     * @return 是否成功
     */
    public boolean upsert(String collectionName, Points.PointStruct point) {
        if (point == null) {
            log.warn("⚠️ 尝试插入空的 Point，操作取消。");
            return false;
        }
        // 直接复用批量插入方法，减少代码冗余
        return upsert(collectionName, List.of(point));
    }

    /**
     * 🚀 批量插入向量数据 (Points)
     *
     * @param collectionName 集合名称 (如 "test")
     * @param uuid         通过 PointBuilder 构建好的数据列表
     */
    public void deletePoint(String collectionName, String uuid) {
        deletePoints(collectionName, List.of(uuid));
    }

    @SuppressWarnings("unchecked")
    public List<RagResultDto> recallRagTo(String collectionName, String queryText){
        return recallMap(collectionName, queryText)
                .stream()
                .map(map -> {
                    RagResultDto dto = new RagResultDto();
                    dto.setId(map.get("id") != null ? map.get("id").toString() : null);
                    if (map.get("score") != null) {
                        dto.setScore(((Number) map.get("score")).floatValue());
                    }
                    if (map.get("body") != null) {
                        dto.setContent((Map<String, Object>) map.get("body"));
                    }
                    dto.setSource(map.get("source") != null ? map.get("source").toString() : collectionName);
                    return dto;
                })
                .collect(Collectors.toList());

    }

    /**
     * 🚀 批量插入向量数据 (Points)
     *
     * @param collectionName 集合名称 (如 "test")
     * @param queryText         内容
     * @return 是否成功
     */
    public List<Map<String, Object>> recallMap(String collectionName, String queryText){
        return recall(collectionName, queryText)
                .stream()
                .map(this::getMapFromPoint)
                .peek(map -> map.put("source", collectionName))
                .collect(Collectors.toList());
    }


    public List<Points.ScoredPoint> recall(String collectionName, String queryText){
        return recall(collectionName, queryText, TOP_K, SCORE);
    }

    /**
     * 获取 ScoredPoint 转换为 Map<String, Object>
     */
    public Map<String, Object> getMapFromPoint(Points.ScoredPoint point){
        return convertMap( point);
    }

    /**
     * 将单层平铺的 ScoredPoint 转换为 Map<String, Object>
     */
    public Map<String, Object> convertMap(Points.ScoredPoint point) {
        Map<String, Object> result = new HashMap<>();
        if (point == null) return result;

        // 1. 注入核心元数据
        if (point.hasId()) {
            result.put("id", point.getId().getUuid());
        }
        result.put("score", point.getScore());

        // 2. 创建一个独立的 body 用来存放所有载体
        Map<String, Object> body = new HashMap<>();

        // 3. 提取单层 Payload 放入 body
        point.getPayloadMap().forEach((key, value) -> {
            switch (value.getKindCase()) {
                case STRING_VALUE:
                    body.put(key, value.getStringValue());
                    break;
                case INTEGER_VALUE:
                    // 安全转为 int（如果数据极大可以保持 value.getIntegerValue() 返回的 long）
                    body.put(key, (int) value.getIntegerValue());
                    break;
                case DOUBLE_VALUE:
                    body.put(key, value.getDoubleValue());
                    break;
                case BOOL_VALUE:
                    body.put(key, value.getBoolValue());
                    break;
                default:
                    break;
            }
        });

        // 4. 将整个 body 作为属性塞进最终的 Map
        result.put("body", body);

        return result;
    }

    /**
     * 🎯 纯语义召回（无任何 Payload 过滤条件）
     *
     * @param collectionName 集合名称 (如 "test")
     * @param queryText      用户的搜索词 (如 "法学分数线")
     * @param limit          希望召回的前 K 条数据 (Top-K, 比如 3)
     * @return 召回的得分点列表（包含 ID、分值、以及完整的 Payload）
     */
    public List<Points.ScoredPoint> recall(String collectionName, String queryText, int limit, float score){
        try {
            float[] vector = openAiEmbeddingModel.embed(queryText).content().vector();
            Points.SearchPoints searchPoints = Points.SearchPoints.newBuilder()
                    .setCollectionName(collectionName)
                    .addAllVector(Floats.asList(vector))
                    .setLimit(limit) // 💡 限制返回数量 (Top-K)
                    .setScoreThreshold(score)
                    .setWithPayload(enable(true))
                    .build();
            return client.searchAsync(searchPoints).get();
        }catch (InterruptedException e) {
            log.error("❌ 向量召回线程被中断: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        } catch (ExecutionException e) {
            log.error("❌ Qdrant 服务端执行检索失败: {}", e.getMessage());
            return new ArrayList<>();
        }

    }

    public boolean upsert(String collectionName, List<Points.PointStruct> points) {
        if (points == null || points.isEmpty()) {
            log.warn("⚠️ 插入的 Points 列表为空，跳过提交。");
            return false;
        }
        try {
            log.info("📦 正在向集合 [{}] 写入 {} 条向量数据...", collectionName, points.size());

            Points.UpdateResult updateResult = client.upsertAsync(collectionName, points).get();

            if (updateResult != null) {
                log.info("✅ 向量成功灌入集合 [{}], 状态: {}", collectionName, updateResult.getStatus());
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            log.error("❌ 向量插入被中断: {}", e.getMessage());
            Thread.currentThread().interrupt(); // 保持中断状态
            return false;
        } catch (ExecutionException e) {
            log.error("❌ Qdrant 服务端拒绝写入，原因: {}", e.getCause().getMessage());
            return false;
        }
    }



    /**
     * 根据 UUID 字符串列表删除指定的 Point
     */
    public void deletePoints(String collectionName, List<String> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return;
        }

        List<Common.PointId> pointIds = uuids.stream()
                .map(uuidStr -> id(UUID.fromString(uuidStr)))
                .toList();

        try {
            // 3. 执行删除（.get() 确保同步等待删除结果返回）
            client.deleteAsync(collectionName, pointIds);
        } catch (Exception e) {
            throw new RuntimeException("Qdrant 删除 Point 失败", e);
        }
    }

}
