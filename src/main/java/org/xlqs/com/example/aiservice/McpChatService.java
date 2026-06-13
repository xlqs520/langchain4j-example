package org.xlqs.com.example.aiservice;

import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午6:37
 * @description McpChatService 类说明：TODO
 * @since 2026/6/13
 */
public interface McpChatService {

    Flux<String> chat(String question);
}
