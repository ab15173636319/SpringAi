package com.sai.common.util;

import lombok.Data;

@Data
public class Result<T> {

    /**
     * 响应码 200成功，其他代表失败
     */
    private Integer code;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;


    /**
     * 成功，带数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功，无返回数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功，自定义消息 + 数据
     */
    public static <T> Result<T> success(String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

    /**
     * 失败，默认code=500
     */
    public static <T> Result<T> fail(String msg) {
        return fail(500, msg);
    }

}
