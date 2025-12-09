package com.finance.util;

// 简化Result创建的工具类
public class ResultUtil {

    public static <T> Result<T> success() {
        return Result.success();
    }

    public static <T> Result<T> success(T data) {
        return Result.success(data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return Result.success(msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return Result.error(code, msg);
    }

    public static <T> Result<T> error(String msg) {
        return Result.error(msg);
    }
}