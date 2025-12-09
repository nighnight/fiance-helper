package com.finance.util;

import lombok.Data;

import java.io.Serializable;

// 通用API返回结果
@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code; // 业务码，200成功，其他失败
    private String msg;   // 提示信息
    private T data;       // 业务数据

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
}