package org.xlqs.com.example.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午10:10
 * @description QdrantConfig 类说明：TODO
 * @since 2026/6/7
 */
@Configuration
public class QdrantConfig {

    @Value("${spring.data.qdrant.host}")
    private String host;

    @Value("${spring.data.qdrant.port}")
    private int port;

    @Value("${spring.data.qdrant.api-key:}")
    private String apiKey;

    @Bean
    public QdrantClient qdrantClient() {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, false);

        if (apiKey != null && !apiKey.isEmpty()) {
            builder.withApiKey(apiKey);
        }

        return new QdrantClient(builder.build());
    }
}
