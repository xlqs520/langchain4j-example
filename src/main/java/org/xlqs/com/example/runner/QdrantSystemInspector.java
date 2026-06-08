package org.xlqs.com.example.runner;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.QdrantOuterClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class QdrantSystemInspector implements CommandLineRunner {

    private final boolean ragEnabled;

    private final QdrantClient client;

    public QdrantSystemInspector(
            @Value("${ai.rag.enabled:false}") boolean ragEnabled,
            Optional<QdrantClient> qdrantClientOptional) {

        this.ragEnabled = ragEnabled;
        // 如果存在就赋值，不存在则为 null
        this.client = qdrantClientOptional.orElse(null);
    }

    @Override
    public void run(String... args) {
        // 💡 3. 如果未开启，直接输出并优雅退出
        if (!ragEnabled || client == null) {
            log.info("ℹ️ 提示: 当前系统【未开启 RAG 知识库功能】，跳过向量库健康检查。");
            return;
        }

        // 💡 4. 开启了 RAG，则执行原本的检查逻辑
        try {
            log.info("ℹ️ 提示: 当前系统【已开启 RAG 知识库功能】，进行向量库健康检查。");
            log.info("🚀 正在获取 Qdrant (v1.17.0+) 服务端信息...");
            QdrantOuterClass.HealthCheckReply health = client.healthCheckAsync().get();
            List<String> collectionNames = client.listCollectionsAsync().get();

            log.info("╔═════════════════ 📊 Qdrant 运行报告 ═════════════════]");
            log.info("║ 🟢 运行状态: Healthy");
            log.info("║ 🌐 服务地址: {}", "https://xlqs.com:6334");
            log.info("║ 🏷️ 服务版本: {}", health.getVersion());
            log.info("║ 📂 集合总数: {}", collectionNames.size());

            if (!collectionNames.isEmpty()) {
                log.info("║ 📦 可用集合: {}", collectionNames);
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