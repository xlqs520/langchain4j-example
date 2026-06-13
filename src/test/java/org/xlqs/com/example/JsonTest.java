package org.xlqs.com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xlqs.com.example.ai.dto.Person;
import org.xlqs.com.example.aiservice.AssistantService;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/10 下午11:52
 * @description JsonTest 类说明：TODO
 * @since 2026/6/10
 */
@SpringBootTest
public class JsonTest {

    @Resource
    private OpenAiChatModel openAiChatModel;


    @Test
    void test() throws JsonProcessingException {
        ResponseFormat responseFormat = ResponseFormat.builder()
                .type(JSON) // type can be either TEXT (default) or JSON
                .jsonSchema(JsonSchema.builder()
                        .name("Person") // OpenAI requires specifying the name for the schema
                        .rootElement(JsonObjectSchema.builder() // see [1] below
                                .addStringProperty("name")
                                .addIntegerProperty("age")
                                .addNumberProperty("height")
                                .addBooleanProperty("married")
                                .required("name", "age", "height", "married") // see [2] below
                                .build())
                        .build())
                .build();

        UserMessage userMessage = UserMessage.from("""
        John is 42 years old and lives an independent life.
        He stands 1.75 meters tall and carries himself with confidence.
        Currently unmarried, he enjoys the freedom to focus on his personal goals and interests.
        """);

        ChatRequest chatRequest = ChatRequest.builder()
                .responseFormat(responseFormat)
                .messages(userMessage)
                .build();

        ChatResponse chatResponse = openAiChatModel.chat(chatRequest);

        String output = chatResponse.aiMessage().text();
        System.out.println(output); // {"name":"John","age":42,"height":1.75,"married":false}

        Person person = new ObjectMapper().readValue(output, Person.class);
        System.out.println(person); // Person[name=John, age=42, height=1.75, married=false]
    }


    @Resource
    private AssistantService assistantService;

    @Test
    void test2() {
        String output = assistantService.generateJson("张三今年28岁，擅长Java和Python编程。");
        System.out.println(output);
    }


    @Test
    void test3() {
        ChatResponse response = openAiChatModel.chat(
                SystemMessage.from("""
                        你是一个信息提取助手。请从以下文本中提取关键信息，并严格以 JSON 格式返回。
                            要求：
                            1. 必须且只能返回一个合法的 JSON 对象。
                            2. JSON 必须包含以下字段：
                               - "name": 字符串，人物姓名
                               - "age": 整数，年龄。如果未提及，请返回 null
                               - "skills": 字符串数组，掌握的技能列表。如果没有，返回空数组 []
                            3. 不要输出任何 markdown 标记（如 ```json），不要输出任何前言或后语。
                        """),
                UserMessage.from("张三今年28岁，擅长Java和Python编程。")
        );
        System.out.println(response);
        String jsonStr = response.aiMessage().text();
        System.out.println(jsonStr);
    }
}
