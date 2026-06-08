package org.xlqs.com.example;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import io.qdrant.client.grpc.Points;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xlqs.com.example.rag.KnowledgeService;
import org.xlqs.com.example.rag.dto.PointBuilder;
import org.xlqs.com.example.util.EmbeddingHelper;
import org.xlqs.com.example.util.JwtHelper;
import org.xlqs.com.example.util.JwtSecretGenerator;
import org.xlqs.com.example.util.QdrantHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ExampleApplicationTests {

    @Test
    void test() {
        JwtSecretGenerator.generate();
    }

    @Resource
    private JwtHelper jwtHelper;

    @Test
    void testJwt() {
        String token = jwtHelper.generateToken("xlqs");
        System.out.println(token);
        System.out.println(jwtHelper.parseToken(token));
    }

    @Resource
    private OpenAiEmbeddingModel openAiEmbeddingModel;

    @Test
    void testEmbedding() {
        Response<Embedding> response = openAiEmbeddingModel.embed("hello world");
        System.out.println(response.content());
    }

    @Resource
    private QdrantHelper qdrantHelper;

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private KnowledgeService knowledgeService;

    @Resource
    private EmbeddingHelper  embeddingHelper;

    @Test
    void testQdrant() {
        qdrantHelper.createCollection("test1");
//        qdrantHelper.deleteCollection("test1");
//        System.out.println(qdrantHelper.getCollectionInfo("test"));
//        System.out.println(Arrays.toString(embeddingModel.embed("hello world").content().vector()));
    }

    @Test
    void testInsert() {
        knowledgeService.upsert("test1", PointBuilder.builder()
                .vector(embeddingHelper.embed("hello world"))
                .payloads(Map.of("name", "xlqs", "age", 18))
                .build()
        );
    }

    @Test
    void testKnowledgeService() {
//        List<Points.ScoredPoint> points = knowledgeService.recall("test", "hello world");
//        System.out.println(points);
//        System.out.println(points);

//        System.out.println(knowledgeService.recallMap("test1", "hello world"));
//        System.out.println(knowledgeService.recallRagTo("test1", "hello world"));
        knowledgeService.deletePoint("test1", "b0242b24-5afc-4f60-8096-1366a698e254");
    }

    @Test
    void testRag() {
        System.out.println(embeddingModel.embed("hello world"));
    }



}
