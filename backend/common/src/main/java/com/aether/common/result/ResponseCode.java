package com.aether.common.result;

import org.springframework.http.HttpStatus;

/**
 * 统一业务状态码。
 * <p>业务状态码与 HTTP 状态码保持一致，前端既可依据响应状态判断，也可只读响应体中的 code。</p>
 */
public enum ResponseCode {
    SUCCESS(200, "操作成功", HttpStatus.OK),
    BAD_REQUEST(400, "请求参数有误", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, "登录状态已失效，请重新登录", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, "没有访问权限", HttpStatus.FORBIDDEN),
    NOT_FOUND(404, "请求的资源不存在", HttpStatus.NOT_FOUND),
    CONFLICT(409, "资源状态冲突", HttpStatus.CONFLICT),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的请求内容类型", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试", HttpStatus.TOO_MANY_REQUESTS),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_GATEWAY(502, "上游服务异常，请稍后重试", HttpStatus.BAD_GATEWAY),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用，请稍后重试", HttpStatus.SERVICE_UNAVAILABLE);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ResponseCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    /**
     * @return 业务状态码
     */
    public int code() {
        return code;
    }

    /**
     * @return 默认提示信息
     */
    public String message() {
        return message;
    }

    /**
     * @return 对应的 HTTP 状态码
     */
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
