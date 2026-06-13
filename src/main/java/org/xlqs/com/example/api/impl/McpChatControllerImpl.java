package org.xlqs.com.example.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.xlqs.com.example.aiservice.McpChatService;
import org.xlqs.com.example.api.McpChatController;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午7:25
 * @description McpChatControllerImpl 类说明：TODO
 * @since 2026/6/13
 */

@RestController
@RequiredArgsConstructor
public class McpChatControllerImpl implements McpChatController {

    private final McpChatService mcpChatService;

    @Override
    public Flux<String> chat(String question) {
        return mcpChatService.chat( question);
    }
}
