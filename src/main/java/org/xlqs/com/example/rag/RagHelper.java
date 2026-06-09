package org.xlqs.com.example.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/9 下午10:30
 * @description RagHelper 类说明：TODO
 * @since 2026/6/9
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class RagHelper {

    private static final int MAX_BATCH_SIZE = 5;

    private final EmbeddingModel embeddingModel;

    private final EmbeddingStore<TextSegment> embeddingStore;


    /**
     * 初始化知识库
     *
     * @param directoryPath 文档路径
     */
    public void initDocuments(String directoryPath) {
        if (directoryPath == null || directoryPath.isBlank()) {
            log.error("❌ 文档初始化失败：入参地址不能为空！");
            return;
        }

        try {
            log.info("🚀 开始初始化知识库，目标地址: {}", directoryPath);

            // 1. 加载指定路径的本地文档
            List<Document> documents = loadLocalDocuments(directoryPath);

            // 2. 切分文档为文本片段
            List<TextSegment> allSegments = splitDocuments(documents);
            log.info("📦 文本切分完成，总片段数: {}，开始分批存入向量库...", allSegments.size());

            // 3. 分批向量化并存储
            saveSegmentsInBatches(allSegments);

            log.info("✅ 知识库初始化成功，共成功存入 {} 条数据。", allSegments.size());
        } catch (Exception e) {
            log.error("❌ 知识库初始化严重失败: ", e);
        }
    }


    /**
     * 步骤 1：根据传入的动态路径加载文档
     */
    private List<Document> loadLocalDocuments(String directoryPath) {
        // 如果传入的是 Spring 的 classpath 相对路径
        if (directoryPath.startsWith("classpath:")) {
            try {
                return FileSystemDocumentLoader.loadDocuments(
                        ResourceUtils.getFile(directoryPath).toPath(),
                        new TextDocumentParser()
                );
            } catch (FileNotFoundException e) {
                throw new RuntimeException("未找到类路径下的文档目录: " + directoryPath, e);
            }
        }

        return FileSystemDocumentLoader.loadDocuments(Paths.get(directoryPath), new TextDocumentParser());
    }

    /**
     * 步骤 2：遍历文档并利用切分器切碎
     */
    private List<TextSegment> splitDocuments(List<Document> documents) {
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
        List<TextSegment> allSegments = new ArrayList<>();

        for (Document doc : documents) {
            List<TextSegment> segments = splitter.split(doc);
            if (segments != null) {
                allSegments.addAll(segments);
            }
        }
        return allSegments;
    }

    /**
     * 步骤 3：核心逻辑——分批提交给 Embedding 模型并写入数据库
     */
    private void saveSegmentsInBatches(List<TextSegment> allSegments) {
        for (int i = 0; i < allSegments.size(); i += MAX_BATCH_SIZE) {
            int end = Math.min(i + MAX_BATCH_SIZE, allSegments.size());
            List<TextSegment> batch = allSegments.subList(i, end);

            log.debug("⏳ 正在处理第 {} 批数据，大小: {}", (i / MAX_BATCH_SIZE + 1), batch.size());

            // 向量化并存入库中
            embeddingStore.addAll(
                    embeddingModel.embedAll(batch).content(),
                    batch
            );
        }
    }
}
