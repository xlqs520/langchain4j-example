package org.xlqs.com.example.util;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午10:49
 * @description QdrantHelper 类说明：TODO
 * @since 2026/6/7
 */
@Service
@Slf4j
@ConditionalOnProperty(prefix = "ai.rag", name = "enabled", havingValue = "true")
public class QdrantHelper {

    private final QdrantClient client;

    public QdrantHelper(QdrantClient client) {
        this.client = client;
    }

    /**
     * @description 创建一个标准的1024维且使用余弦相似度的集合,如果已存在则跳过.
     *
     * @param collectionName 集合名称
     */
    public void createCollection(String collectionName) {
        createCollection(collectionName, 1024, Collections.Distance.Cosine);
    }

    /**
     * @description 创建一个标准的集合,如果已存在则跳过.
     *
     * @param collectionName 集合名称
     * @param dimension 集合维度
     * @param distance 集合距离算法
     */
    public void createCollection(String collectionName, int dimension, Collections.Distance distance) {

        try {
            List<String> existingCollections = client.listCollectionsAsync().get();

            if (existingCollections.contains(collectionName)) {
                log.info("ℹ️ Qdrant 集合 [{}] 已存在，跳过创建。", collectionName);
                return;
            }
            log.info("🚀 正在创建 Qdrant 集合: 【{}】, 维度: {}, 算法: {}",
                    collectionName, dimension, distance);
            // 2. 构造创建参数
            Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                    .setSize(dimension)
                    .setDistance(distance)
                    .build();

            client.createCollectionAsync(collectionName, vectorParams).get();

            log.info("✅ 集合 [{}] 创建成功！", collectionName);

        } catch (InterruptedException e) {
            log.error("❌ 创建集合线程中断: {}", e.getMessage());
            Thread.currentThread().interrupt();
            throw new RuntimeException("创建集合失败，线程被中断", e);
        } catch (ExecutionException e) {
            log.error("❌ Qdrant 服务端执行创建失败: {}", e.getMessage());
            throw new RuntimeException("Qdrant 执行异常", e);
        }
    }

    /**
     * 安全删除集合
     *
     * @param collectionName 集合名称
     * @return 是否删除成功
     */
    public boolean deleteCollection(String collectionName) {
        try {
            log.warn("🚀 正在尝试删除 Qdrant 集合: [{}]", collectionName);
            client.deleteCollectionAsync(collectionName).get();
            log.info("✅ 集合 [{}] 删除成功！", collectionName);
            return true;
        } catch (Exception e) {
            log.error("❌ 删除集合 [{}] 失败: {}", collectionName, e.getMessage());
            return false;
        }
    }


    /**
     * 获取集合的详细信息（可用于检查当前集合有多少条数据）
     */
    public Collections.CollectionInfo getCollectionInfo(String collectionName) {
        try {
            return client.getCollectionInfoAsync(collectionName).get();
        } catch (Exception e) {
            log.error("❌ 获取集合 [{}] 信息失败: {}", collectionName, e.getMessage());
            return null;
        }
    }


}
