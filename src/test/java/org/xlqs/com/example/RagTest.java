package org.xlqs.com.example;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/9 下午10:03
 * @description RagTest 类说明：TODO
 * @since 2026/6/9
 */
@SpringBootTest
public class RagTest {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Test
    public void initDocuments() {
        try {
            // 1. 加载文档
            List<Document> documents = FileSystemDocumentLoader.loadDocuments(
                    Paths.get("D:\\idea_project\\langchain4j\\second\\src\\main\\resources\\content"),
                    new TextDocumentParser()
            );

            // 2. 切分器
            DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);

            List<TextSegment> allSegments = new ArrayList<>();

            // 3. 对每个文档进行切分
            for (Document doc : documents) {
                List<TextSegment> segments = splitter.split(doc);
                allSegments.addAll(segments);
            }

            System.out.println("📦 文本切分完成，总片段数: " + allSegments.size() + "，开始分批存入向量库...");

            // ================== 🔥 核心优化：分批提交 ==================
            int MAX_BATCH_SIZE = 5; // 既然模型限制 max 10，我们用 5 个一批最稳妥

            for (int i = 0; i < allSegments.size(); i += MAX_BATCH_SIZE) {
                // 计算当前批次的结束位置，防止越界
                int end = Math.min(i + MAX_BATCH_SIZE, allSegments.size());
                // 截取当前批次的片段
                List<TextSegment> batchSegments = allSegments.subList(i, end);

                System.out.println("⏳ 正在处理第 " + (i / MAX_BATCH_SIZE + 1) + " 批数据，大小: " + batchSegments.size());

                // 单批次生成向量并存入向量库
                embeddingStore.addAll(
                        embeddingModel.embedAll(batchSegments).content(),
                        batchSegments
                );
            }
            // =========================================================

            System.out.println("✅ 初始化完成，成功切分并存入 " + allSegments.size() + " 条数据。");

        } catch (Exception e) {
            System.err.println("❌ 文档初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
