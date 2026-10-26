package com.aether.common.error;

import com.aether.common.result.ResponseCode;
import com.aether.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Result<Void>> handleApiException(ApiException exception) {
        HttpStatus status = exception.getStatus();
        if (status.is5xxServerError()) {
            log.error("业务服务异常：{}", exception.getMessage(), exception);
        } else {
            log.warn("业务请求失败：{}", exception.getMessage());
        }
        return ResponseEntity.status(status)
                .body(Result.error(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .distinct()
                .collect(Collectors.joining("；"));
        return ResponseEntity.badRequest()
                .body(Result.error(ResponseCode.BAD_REQUEST, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .collect(Collectors.joining("；"));
        return ResponseEntity.badRequest()
                .body(Result.error(ResponseCode.BAD_REQUEST, message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleUnreadableBody() {
        return ResponseEntity.badRequest()
                .body(Result.error(ResponseCode.BAD_REQUEST, "请求内容格式不正确"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Result<Void>> handleUnsupportedMediaType() {
        return ResponseEntity.status(ResponseCode.UNSUPPORTED_MEDIA_TYPE.httpStatus())
                .body(Result.error(ResponseCode.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Result<Void>> handleDatabase(DataAccessException exception) {
        log.error("数据库操作失败", exception);
        return ResponseEntity.status(ResponseCode.SERVICE_UNAVAILABLE.httpStatus())
                .body(Result.error(
                        ResponseCode.SERVICE_UNAVAILABLE,
                        "数据库暂时不可用，请稍后重试"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpected(Exception exception) {
        log.error("未预期的应用异常", exception);
        return ResponseEntity.status(ResponseCode.SYSTEM_ERROR.httpStatus())
                .body(Result.error(ResponseCode.SYSTEM_ERROR));
    }
}


