package org.xlqs.com.example.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.xlqs.com.example.util.JwtHelper;

import java.io.IOException;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午11:19
 * @description AuthFilter 类说明：TODO
 * @since 2026/6/6
 */
@Component
public class AuthFilter implements Filter {

    private static final String TOKEN_HEADER = "X-Auth-Token";

    @Resource
    private JwtHelper jwtHelper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;


        String path = req.getRequestURI();

        // ====== 放行接口（比如登录、Swagger）======
        if (path.contains("/login")
                || path.contains("/swagger")
                || path.contains("/v3/api-docs")) {
            chain.doFilter(request, response);
            return;
        }

        String token = req.getHeader(TOKEN_HEADER);

//        if (!checkToken( token)) {
//            writeError(resp);
//            return;
//        }

        chain.doFilter(request, response);
    }

    private void writeError(HttpServletResponse resp) throws IOException {
        resp.setStatus(401);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(
                "{\"code\":" + 401 + ",\"msg\":\"" + "缺少 X-Auth-Token" + "\",\"data\":null}"
        );
    }

    private boolean checkToken(String token) {

        if (!jwtHelper.validateToken(token)) {
            return false;
        }
        return redisTemplate.hasKey(token);
    }

}
