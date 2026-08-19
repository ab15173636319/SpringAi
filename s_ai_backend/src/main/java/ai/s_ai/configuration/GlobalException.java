package ai.s_ai.configuration;

import ai.s_ai.utils.ResultUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    // 处理参数校验异常（@Valid @RequestBody）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultUtil<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        String msg = methodArgumentNotValidException.getBindingResult()
                .getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("， "));
        return ResultUtil.fail(msg);
    }


    // 处理参数绑定异常（表单提交）
    @ExceptionHandler(BindException.class)
    public ResultUtil<Void> handleBindException(BindException bindException) {
        String msg = bindException.getBindingResult()
                .getFieldErrors()
                .stream().map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("， "));
        return ResultUtil.fail(msg);
    }

    /**
     * 处理单参数校验异常（@Validated @RequestParam）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResultUtil<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResultUtil.fail(400, msg);
    }

    /**
     * 处理请求体解析异常（例如 JSON 格式错误）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultUtil<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResultUtil.fail(400, "请求体格式错误，请检查 JSON 格式或参数类型");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResultUtil<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResultUtil.fail(400, e.getMessage());
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResultUtil<Void> handleNullPointerException(NullPointerException e) {
        return ResultUtil.fail(500, "系统内部错误：空指针异常");
    }

    /**
     * 处理 MongoDB 相关运行时异常
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResultUtil<Void> handleDataAccessException(org.springframework.dao.DataAccessException e) {
        String msg = e.getMessage();
        // 提取更友好的错误提示
        if (msg != null && msg.contains("BadValue")) {
            return ResultUtil.fail(500, "数据库参数配置错误: " + msg);
        }
        return ResultUtil.fail(500, "数据库访问异常，请稍后重试");
    }

    /**
     * 处理自定义业务异常（预留扩展）
     */
    @ExceptionHandler(BusinessException.class)
    public ResultUtil<Void> handleBusinessException(BusinessException e) {
        return ResultUtil.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常（兜底未被上面捕获的 RuntimeException）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResultUtil<Void> handleRuntimeException(RuntimeException e) {
        return ResultUtil.fail(500, "服务器运行错误: " + e.getMessage());
    }

    /**
     * 全局兜底异常处理（捕获所有未被处理的 Exception）
     */
    @ExceptionHandler(Exception.class)
    public ResultUtil<Void> handleException(Exception e) {
        return ResultUtil.fail(500, "服务器内部错误，请联系管理员");
    }

    /**
     * 自定义业务异常类（内部静态类，方便项目中直接使用）
     */
    public static class BusinessException extends RuntimeException {
        private final Integer code;

        public BusinessException(String msg) {
            super(msg);
            this.code = 500;
        }

        public BusinessException(Integer code, String msg) {
            super(msg);
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }
}
