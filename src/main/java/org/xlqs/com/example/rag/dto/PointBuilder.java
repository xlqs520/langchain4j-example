package org.xlqs.com.example.rag.dto;

import io.qdrant.client.PointIdFactory;
import io.qdrant.client.ValueFactory;
import io.qdrant.client.VectorFactory;
import io.qdrant.client.VectorsFactory;
import io.qdrant.client.grpc.Points;

import java.util.Map;
import java.util.UUID;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午11:12
 * @description PointBuilder 类说明：TODO
 * @since 2026/6/7
 */
public class PointBuilder {

    Points.PointStruct.Builder builder = Points.PointStruct.newBuilder();

    private PointBuilder() {
    }

    public static PointBuilder builder() {
        return new PointBuilder();
    }

    public PointBuilder vector(float[] vector) {
        builder.setVectors(VectorsFactory.vectors(VectorFactory.vector(vector)));
        return this;
    }

    public PointBuilder id(String uuidStr) {
        builder.setId(PointIdFactory.id(UUID.fromString(uuidStr)));
        return this;
    }

    public PointBuilder payload(String key, Object value) {
        if (value != null) {
            // 💡 核心优化：动态解析真实类型，确保 Qdrant 索引不失效
            if (value instanceof String) {
                builder.putPayload(key, ValueFactory.value((String) value));
            } else if (value instanceof Integer || value instanceof Long) {
                builder.putPayload(key, ValueFactory.value(((Number) value).longValue()));
            } else if (value instanceof Float || value instanceof Double) {
                builder.putPayload(key, ValueFactory.value(((Number) value).doubleValue()));
            } else if (value instanceof Boolean) {
                builder.putPayload(key, ValueFactory.value((Boolean) value));
            } else {
                builder.putPayload(key, ValueFactory.value(value.toString()));
            }
        }
        return this;
    }

    public PointBuilder payloads(Map<String, Object> metadata) {
        if (metadata != null && !metadata.isEmpty()) {
            metadata.forEach(this::payload);
        }
        return this;
    }

    public Points.PointStruct build() {
        // 💡 如果用户从头到尾没有调用过 .id(...)，这里才去触发随机生成
        if (!builder.hasId()) {
            builder.setId(PointIdFactory.id(UUID.randomUUID()));
        }
        return builder.build();
    }
}
