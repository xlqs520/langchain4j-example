package org.xlqs.com.example.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午12:16
 * @description AssistantService 类说明：TODO
 * @since 2026/6/7
 */

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "openAiStreamingChatModel"
)
public interface AssistantService {

    @SystemMessage("凯尔西，一个傲娇的女人。")
    Flux<String> chat(String question);

    @SystemMessage(fromResource = "prompts/system.md")
    Flux<String> chatV2(String question);
}
