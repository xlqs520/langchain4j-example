package org.xlqs.com.example.api.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.xlqs.com.example.api.LoginController;
import org.xlqs.com.example.util.JwtHelper;
import org.xlqs.com.example.vo.LoginVo;
import org.xlqs.com.example.vo.Result;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午10:43
 * @description LoginControllerImpl 类说明：TODO
 * @since 2026/6/6
 */
@RestController
@RequiredArgsConstructor
public class LoginControllerImpl implements LoginController {

    private final JwtHelper jwtHelper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.skip}")
    private Boolean enabled;

    @Override
    public Result<String> login(HttpServletResponse response,LoginVo loginVo) {

        if (loginVo.getUsername() == null || loginVo.getPassword() == null){
            return Result.error("用户名或密码不能为空");
        }

        if (loginVo.getUsername().equals("xlqs") && loginVo.getPassword().equals("xlqsnhyq521..")) {
            String Authorization = jwtHelper.generateToken(loginVo.getUsername());
            response.addHeader("X-Auth-Token", Authorization);
            if (!enabled) {
                redisTemplate.opsForHash().putAll(Authorization, jwtHelper.parseToken(Authorization));
            }
            return Result.success("登录成功");
        }

        return Result.error("用户名或密码错误");
    }

    @Override
    public Result<String> logout(HttpServletRequest  request) {
        String x_auth_token = request.getHeader("X-Auth-Token");
        redisTemplate.delete(x_auth_token);
        System.out.println(x_auth_token);
        return Result.success("登出成功");
    }

}
