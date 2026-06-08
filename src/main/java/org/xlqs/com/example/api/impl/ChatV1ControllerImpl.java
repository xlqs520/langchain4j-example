package org.xlqs.com.example.api.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.xlqs.com.example.aiservice.AssistantService;
import org.xlqs.com.example.api.ChatV1Controller;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午7:24
 * @description ChatV1ControllerImpl 类说明：TODO
 * @since 2026/6/7
 */

@RestController
@RequiredArgsConstructor
public class ChatV1ControllerImpl implements ChatV1Controller {

    private final AssistantService assistantService;

    @Override
    public Flux<String> chatV1(String question) {
        return assistantService.chat(question);
    }

    @Override
    public Flux<String> chatV2(String question) {
        return assistantService.chatV2( question);
    }
}
