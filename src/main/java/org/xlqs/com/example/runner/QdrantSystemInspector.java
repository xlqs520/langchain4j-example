package org.xlqs.com.example.runner;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.QdrantOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午10:13
 * @description QdrantSystemInspector 类说明：TODO
 * @since 2026/6/7
 */

@Component
@Slf4j
public class QdrantSystemInspector implements CommandLineRunner {

    private final QdrantClient client;

    public QdrantSystemInspector(QdrantClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        try {
            log.info("🚀 正在获取 Qdrant (v1.17.0+) 服务端信息...");

            // 1. 获取健康状况和版本
            QdrantOuterClass.HealthCheckReply health = client.healthCheckAsync().get();

            // 2. 获取集合摘要
            List<String> collectionNames = client.listCollectionsAsync().get();

            log.info("╔═════════════════ 📊 Qdrant 运行报告 ═════════════════]");
            log.info("║ 🟢 运行状态: Healthy");
            log.info("║ 🌐 服务地址: {}", "https://xlqs.com:6334");
            log.info("║ 🏷️ 服务版本: {}", health.getVersion());
            log.info("║ 📂 集合总数: {}", collectionNames.size());

            if (!collectionNames.isEmpty()) {
                log.info("║ 📦 可用集合: {}", collectionNames);

                // 进阶：打印第一个集合的详细点数
                String firstColl = collectionNames.getFirst();
                var info = client.getCollectionInfoAsync(firstColl).get();
                log.info("║ 🔍 抽检集合: [{}] -> 📈 向量点数: {}", firstColl, info.getPointsCount());
            } else {
                log.info("║ 📦 可用集合: [ ⚠️ 暂无任何集合 ]");
            }
            log.info("╚═════════════════════════════════════════════════════]");

        } catch (Exception e) {
            log.error("❌ Qdrant 连接异常！请检查 Docker 容器或网络配置。");
            log.error("   └─ 📑 异常详情: {}", e.getMessage());
        }
    }
}
