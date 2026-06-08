package org.xlqs.com.example.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.xlqs.com.example.vo.LoginVo;
import org.xlqs.com.example.vo.Result;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午10:41
 * @description LoginControllerImpl 类说明：TODO
 * @since 2026/6/6
 */
@RequestMapping("")
public interface LoginController {

    /**
     * 登录
     *
     * @param loginVo  登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    Result<String> login(HttpServletResponse response, @RequestBody LoginVo loginVo);

    /**
     * 登出
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    Result<String> logout(HttpServletRequest  request);
}
