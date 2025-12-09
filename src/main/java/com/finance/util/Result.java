package com.finance.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "后端统一返回结果")
public class Result<T> implements Serializable {

    @Schema(description = "编码：200成功，500和其他数字为失败")
    private Integer code;

    @Schema(description = "错误信息")
    private String msg;

    @Schema(description = "数据")
    private T data;

    // --- 成功响应的方法 ---

    /**
     * 成功，无返回数据
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功";
        return result;
    }

    /**
     * 成功，返回数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 成功，自定义消息（一般比较少用，但保留兼容性）
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.msg = msg;
        result.data = data;
        return result;
    }

    // --- 失败响应的方法 ---

    /**
     * 失败，自定义错误信息
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.msg = msg;
        return result;
    }
}