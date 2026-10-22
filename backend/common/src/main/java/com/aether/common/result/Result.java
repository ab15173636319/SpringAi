package com.aether.common.result;

import java.time.Instant;

/**
 * 通用接口返回结果。
 *
 * @param code      业务状态码
 * @param message   提示信息
 * @param data      返回数据
 * @param timestamp 响应时间
 * @param <T>       返回数据类型
 */
public record Result<T>(int code, String message, T data, Instant timestamp) {

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, Instant.now());
    }

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null, Instant.now());
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
