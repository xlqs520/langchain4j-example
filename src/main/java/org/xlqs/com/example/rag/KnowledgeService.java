package org.xlqs.com.example.rag;

import com.google.common.primitives.Floats;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public boolean upsertPoint(String collectionName, Points.PointStruct point) {
        if (point == null) {
            log.warn("⚠️ 尝试插入空的 Point，操作取消。");
            return false;
        }
        // 直接复用批量插入方法，减少代码冗余
        return upsertPoints(collectionName, List.of(point));
    }


    public List<Points.ScoredPoint> recall(String collectionName, String queryText){
        return recall(collectionName, queryText, TOP_K, SCORE);
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

    public boolean upsertPoints(String collectionName, List<Points.PointStruct> points) {
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

}
