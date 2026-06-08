package org.xlqs.com.example.api.impl;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.xlqs.com.example.aiservice.AssistantService;
import org.xlqs.com.example.aiservice.ConsultantService;
import org.xlqs.com.example.api.ChatController;
import org.xlqs.com.example.util.ChatService;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午5:32
 * @description ChatControllerImpl 类说明：TODO
 * @since 2026/6/6
 */

@RestController
@RequiredArgsConstructor
public class ChatControllerImpl implements ChatController {

    private final OpenAiChatModel openAiChatModel;

    private final OpenAiStreamingChatModel openAiStreamingChatModel;

    private final ConsultantService consultantService;

    private final AssistantService assistantService;

    @Override
    public String test(String question) {
        return openAiChatModel.chat( question);
    }

    @Override
    public Flux<String> stream(String question) {

        return Flux.create(sink -> {

            openAiStreamingChatModel.chat(
                    question,
                    new StreamingChatResponseHandler() {

                        @Override
                        public void onPartialResponse(String token) {
                            sink.next(token); // 推送数据
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse response) {
                            sink.complete(); // 完成
                        }

                        @Override
                        public void onError(Throwable error) {
                            sink.error(error); // 错误
                        }
                    }
            );

        });
    }

    @Override
    public SseEmitter chatV2(String question) {
        Flux<String> chatStream = stream( question);
        return ChatService.chat(chatStream);
    }

    @Override
    public void chatNoData(String question) {
        Flux<String> chatStream = stream( question);
        ChatService.chatStream(chatStream);
    }

    @Override
    public String chatWithAiService(String question) {
        return consultantService.chat(question);
    }

    @Override
    public SseEmitter chatV5(String question) {
        Flux<String> chat = assistantService.chat(question);
        return ChatService.chat(chat);
    }

    @Override
    public Flux<String> flux(String question) {
        return assistantService.chat(question);
    }
}
