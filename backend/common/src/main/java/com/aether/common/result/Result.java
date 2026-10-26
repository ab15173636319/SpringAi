package com.aether.common.result;

/**
 * 通用接口返回结果。
 *
 * @param code    业务状态码
 * @param message 提示信息
 * @param data    返回数据
 * @param <T>     返回数据类型
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> success(T data) {
        return success("操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResponseCode.SUCCESS.code(), message, data);
    }

    public static Result<Void> success() {
        return success(null);
    }

    public static <T> Result<T> error(ResponseCode responseCode) {
        return error(responseCode, responseCode.message());
    }

    public static <T> Result<T> error(ResponseCode responseCode, String message) {
        return new Result<>(responseCode.code(), message, null);
    }
}
