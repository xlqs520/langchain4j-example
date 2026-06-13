package org.xlqs.com.example.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xlqs.com.example.ai.dto.Person;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/13 下午5:56
 * @description StructuredController 类说明：TODO
 * @since 2026/6/13
 */
@RequestMapping("/structured")
@Tag(name = "结构化接口", description = "结构化接口")
public interface StructuredController {

    @GetMapping("/json")
    String json();

    @GetMapping("/jsonV2")
    Person jsonV2();
}
