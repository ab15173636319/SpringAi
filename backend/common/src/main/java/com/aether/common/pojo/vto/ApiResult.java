package com.aether.common.pojo.vto;

import java.time.Instant;

public record ApiResult<T>(int code, String message, T data, Instant timestamp) {

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "操作成功", data, Instant.now());
    }

    public static ApiResult<Void> success() {
        return success(null);
    }

    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message, null, Instant.now());
    }
}



