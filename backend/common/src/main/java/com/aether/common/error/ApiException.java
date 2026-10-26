package com.aether.common.error;

import com.aether.common.result.ResponseCode;
import com.aether.common.result.Result;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 自定义业务异常：携带返回给客户端的 HTTP 状态码、业务状态码与提示信息，
 * 由 {@link GlobalExceptionHandler} 统一转换成 {@link Result} 响应体。
 * <p>优先使用静态工厂方法（语义清晰、避免状态码写错）；需要任意状态码时使用
 * {@link #ApiException(HttpStatus, String)}。异常信息会直接作为提示返回给前端，请勿写入敏感内容。</p>
 */
@Getter
public class ApiException extends RuntimeException {

    /**
     * 响应使用的 HTTP 状态码
     */
    private final HttpStatus status;

    /**
     * 响应体中的业务状态码
     */
    private final int code;

    /**
     * 以预定义状态码创建异常。
     *
     * @param responseCode 业务状态码
     * @param message      提示信息
     */
    public ApiException(ResponseCode responseCode, String message) {
        this(responseCode.httpStatus(), responseCode.code(), message, null);
    }

    /**
     * 以预定义状态码创建异常，并保留原始异常便于排查。
     *
     * @param responseCode 业务状态码
     * @param message      提示信息
     * @param cause        原始异常
     */
    public ApiException(ResponseCode responseCode, String message, Throwable cause) {
        this(responseCode.httpStatus(), responseCode.code(), message, cause);
    }

    /**
     * 以原始 HTTP 状态码创建异常，业务状态码取 HTTP 状态码的值。
     *
     * @param status  HTTP 状态码
     * @param message 提示信息
     */
    public ApiException(HttpStatus status, String message) {
        this(status, status.value(), message, null);
    }

    /**
     * 以原始 HTTP 状态码创建异常，并保留原始异常便于排查。
     *
     * @param status  HTTP 状态码
     * @param message 提示信息
     * @param cause   原始异常
     */
    public ApiException(HttpStatus status, String message, Throwable cause) {
        this(status, status.value(), message, cause);
    }

    private ApiException(HttpStatus status, int code, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.code = code;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(ResponseCode.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(ResponseCode.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(ResponseCode.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(ResponseCode.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(ResponseCode.CONFLICT, message);
    }

    public static ApiException tooManyRequests(String message) {
        return new ApiException(ResponseCode.TOO_MANY_REQUESTS, message);
    }

    public static ApiException systemError(String message) {
        return new ApiException(ResponseCode.SYSTEM_ERROR, message);
    }

    public static ApiException badGateway(String message) {
        return new ApiException(ResponseCode.BAD_GATEWAY, message);
    }

    public static ApiException serviceUnavailable(String message) {
        return new ApiException(ResponseCode.SERVICE_UNAVAILABLE, message);
    }

    /**
     * 包装其他异常，保留其堆栈用于排查，对外只暴露统一的错误提示。
     *
     * @param status  响应状态码
     * @param message 对外提示信息
     * @param cause   原始异常
     * @return 携带原始异常的 ApiException
     */
    public static ApiException wrap(HttpStatus status, String message, Throwable cause) {
        return new ApiException(status, message, cause);
    }
}
