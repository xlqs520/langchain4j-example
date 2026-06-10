package org.xlqs.com.example.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/9 下午11:20
 * @description HealthController 类说明：TODO
 * @since 2026/6/9
 */

@RequestMapping("health")
public interface HealthController {

    @GetMapping("/check")
    Map<String, Object> healthCheck(HttpServletRequest req);
}
