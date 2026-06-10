package org.xlqs.com.example.api.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.xlqs.com.example.api.HealthController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/9 下午11:21
 * @description HealthControllerImpl 类说明：TODO
 * @since 2026/6/9
 */
@RestController
public class HealthControllerImpl implements HealthController {
    @Override
    public Map<String, Object> healthCheck(HttpServletRequest req) {
        Map<String, Object> map = new HashMap<>();

        map.put("scheme", req.getScheme());
        map.put("serverName", req.getServerName());
        map.put("serverPort", req.getServerPort());

        Enumeration<String> names = req.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, req.getHeader(name));
        }

        return map;
    }
}
