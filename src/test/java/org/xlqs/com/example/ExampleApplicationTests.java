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

    @Test
    void testQdrant() {
        qdrantHelper.createCollection("test1");
//        qdrantHelper.deleteCollection("test1");
//        System.out.println(qdrantHelper.getCollectionInfo("test"));
        System.out.println(Arrays.toString(embeddingModel.embed("hello world").content().vector()));
    }

    @Test
    void testKnowledgeService() {
//        knowledgeService.upsertPoint("test", PointBuilder.builder()
//                        .vector(embeddingModel.embed("hello world").content().vector())
//                .payloads(Map.of("name", "xlqs", "age", 18))
//                .build()
//        );
        List<Points.ScoredPoint> points = knowledgeService.recall("test", "hello world", 3, 0.1f);
        System.out.println(points);
    }


}
