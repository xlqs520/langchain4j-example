package org.xlqs.com.example.ai.dto;

import dev.langchain4j.model.output.structured.Description;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/10 下午11:48
 * @description PersonInfo 类说明：TODO
 * @since 2026/6/10
 */
@Description("包含用户基本信息的结果对象")
public record Person(
        @Description("用户的姓名") String name,
        @Description("用户的年龄") int age,
        @Description("用户的身高") String height,
        @Description("用户的婚姻") String married
) {}
