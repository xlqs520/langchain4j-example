package org.xlqs.com.example.util;

import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/8 下午10:44
 * @description EmbeddingHelper 类说明：TODO
 * @since 2026/6/8
 */

@Service
@RequiredArgsConstructor
public class EmbeddingHelper {

    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        return embeddingModel.embed(text).content().vector();
    }
}
