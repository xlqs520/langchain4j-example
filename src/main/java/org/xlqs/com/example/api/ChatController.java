package org.xlqs.com.example.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午5:21
 * @description ChatController 类说明：TODO
 * @since 2026/6/6
 */

@RequestMapping("/chat1")
@Tag(name = "ChatController", description = "聊天接口 default")
@Hidden
public interface ChatController {

    /**
     * 非流式会话
     *
     * @param question  问题
     * @return Ai回答
     */
    @GetMapping("/test")
    @Hidden
    String test(String question);

    /**
     * 流式会话
     *
     * @param question  问题
     * @return Ai回答
     */
    @GetMapping(value = "/stream")
    @Hidden
    Flux<String> stream(String question);

    /**
     * 流式会话
     *
     * @param question  问题
     */
    @GetMapping(value = "/chatV2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "流式会话")
    SseEmitter chatV2(@RequestParam String question);

    /**
     * 流式会话（纯文本，无 data: 前缀）
     *·
     * @param question  问题
     */
    @GetMapping(value = "/chatV3")
    @Hidden
    @Operation(summary = "流式会话（纯文本，无 data: 前缀）")
    void chatNoData(@RequestParam String question);

    /**
     * 对话使用AiService手动
     *
     * @param question 问题
     * @return 对话
     */
    @GetMapping("/chatV4")
    @Operation(summary = "对话使用AiService手动")
    @Hidden
    String chatWithAiService(@RequestParam String question);

    /**
     * 对话使用AiService
     *
     * @param question 问题
     */
    @GetMapping("/chatV5")
    @Operation(summary = "对话使用AiService")
    SseEmitter chatV5(@RequestParam String question);


    /**
     * flux模式
     *
     * @param question 问题
     * @return 响应流
     */
    @GetMapping(value = "/flux")
    Flux<String> flux(@RequestParam String question);
}
