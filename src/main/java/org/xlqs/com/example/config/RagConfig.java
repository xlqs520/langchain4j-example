package org.xlqs.com.example.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/8 下午11:17
 * @description RagConfig 类说明：TODO
 * @since 2026/6/8
 */
@Configuration
@ConditionalOnExpression("${ai.rag.enabled:false}")
public class RagConfig {

    @Value("${spring.data.qdrant.api-key}")
    private String apiKey;

    @Value("${spring.data.qdrant.host}")
    private String host;

    @Value("${spring.data.qdrant.port}")
    private Integer port;




    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return QdrantEmbeddingStore
                .builder()
                .host(host)
                .port(6334)
                .useTls(false)
                .apiKey(apiKey)
                .collectionName("test1")
                .build();
    }

        @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                             EmbeddingModel embeddingModel){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.3)
                .build();
    }


}
