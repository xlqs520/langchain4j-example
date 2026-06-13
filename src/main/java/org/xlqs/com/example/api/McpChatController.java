package org.xlqs.com.example.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午7:24
 * @description McpChatController 类说明：TODO
 * @since 2026/6/13
 */
@RequestMapping("/mcp")
public interface McpChatController {

    @GetMapping("/chat")
    Flux<String> chat(String question);
}
