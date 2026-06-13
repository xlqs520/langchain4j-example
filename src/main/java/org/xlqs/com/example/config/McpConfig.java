package org.xlqs.com.example.config;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xlqs.com.example.aiservice.McpChatService;

import java.util.Map;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午6:42
 * @description McpConfig 类说明：TODO
 * @since 2026/6/13
 */
@Configuration
public class McpConfig {

    @Value("${ai.mcp.github.key}")
    private String githubKey;

    @Value("${ai.mcp.github.url}")
    private String githubMcpUrl;

    @Value("${ai.mcp.github.token}")
    private String githubMcpToken;

    @Bean
    public McpChatService mcpChatService(OpenAiStreamingChatModel openAiStreamingChatModel,
            McpToolProvider mcpToolProvider) {
        return AiServices.builder(McpChatService.class)
                .streamingChatModel(openAiStreamingChatModel)
                .toolProvider(mcpToolProvider)
                .build();
    }

    @Bean
    public McpToolProvider mcpToolProvider() {

        McpTransport transport = StreamableHttpMcpTransport
                .builder()
                .url(githubMcpUrl)
                .customHeaders(this::addHeader)
                .build();

        McpClient mcpClient = new DefaultMcpClient
                .Builder()
                .key(githubKey)
                .transport(transport)
                .build();


        return McpToolProvider.builder()
                .mcpClients(mcpClient)
                .build();
    }

    private Map<String, String> addHeader(){
        return Map.of("Authorization", githubMcpToken);
    }
}
