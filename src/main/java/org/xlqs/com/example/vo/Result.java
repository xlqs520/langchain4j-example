package org.xlqs.com.example.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Schema(description = "响应类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    @Schema(description = "状态码：200成功 / 500失败")
    private Integer code;

    @Schema(description = "返回信息")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    // ================= 成功 =================

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // ================= 失败 =================

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

}