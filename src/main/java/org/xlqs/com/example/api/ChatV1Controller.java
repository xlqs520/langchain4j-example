package org.xlqs.com.example.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午7:18
 * @description ChatV1Controller 类说明：TODO
 * @since 2026/6/7
 */
@RequestMapping("/v1/chat")
@Tag(name = "对话接口 v1", description = "聊天接口 v1")
public interface ChatV1Controller {

    @GetMapping("/v1")
    @Operation(summary = "对话接口")
    Flux<String> chatV1(String question);

    @GetMapping("/v2")
    @Operation(summary = "对话接口 v2")
    Flux<String> chatV2(String question);

}
