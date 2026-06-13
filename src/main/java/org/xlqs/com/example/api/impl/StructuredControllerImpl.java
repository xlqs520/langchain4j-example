package org.xlqs.com.example.api.impl;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.xlqs.com.example.ai.dto.Person;
import org.xlqs.com.example.aiservice.AssistantService;
import org.xlqs.com.example.api.StructuredController;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午6:09
 * @description StructuredControllerImpl 类说明：TODO
 * @since 2026/6/13
 */
@RestController
@RequiredArgsConstructor
public class StructuredControllerImpl implements StructuredController {

    private final AssistantService assistantService;

    @Override
    public String json() {
        return assistantService.generateJson("张三今年28岁，擅长Java和Python编程。");
    }

    @Override
    public Person jsonV2() {
        return assistantService.generatePerson("""
        John is 42 years old and lives an independent life.
        He stands 1.75 meters tall and carries himself with confidence.
        Currently unmarried, he enjoys the freedom to focus on his personal goals and interests.
        """);
    }
}
