package org.xlqs.com.example.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xlqs.com.example.aiservice.ConsultantService;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 上午11:59
 * @description ConsultantConfig 类说明：TODO
 * @since 2026/6/7
 */

@Configuration
public class ConsultantConfig {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Bean
    public ConsultantService consultantService() {
        return AiServices.builder(ConsultantService.class)
                .chatModel(openAiChatModel)
                .build();
    }
}
