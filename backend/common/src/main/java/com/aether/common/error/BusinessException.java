package com.aether.common.error;

import com.aether.common.result.ResponseCode;

/**
 * 兼容旧调用点的业务异常，能力与 {@link ApiException} 一致。
 *
 * @deprecated 请改用 {@link ApiException} 及其静态工厂方法，例如 {@code ApiException.unauthorized("...")}
 */
@Deprecated
public class BusinessException extends ApiException {

    public BusinessException(ResponseCode responseCode, String message) {
        super(responseCode, message);
    }

    public BusinessException(ResponseCode responseCode, String message, Throwable cause) {
        super(responseCode, message, cause);
    }
}
