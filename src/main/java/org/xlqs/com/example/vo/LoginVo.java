package org.xlqs.com.example.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午10:48
 * @description LoginVo 类说明：TODO
 * @since 2026/6/6
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "用户登录实体类")
public class LoginVo {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;
}
