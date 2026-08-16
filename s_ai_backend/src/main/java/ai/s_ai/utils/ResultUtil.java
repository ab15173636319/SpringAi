package ai.s_ai.utils;

import lombok.Data;

@Data
public class ResultUtil<T> {

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
    public static <T> ResultUtil<T> success(T data) {
        ResultUtil<T> ResultUtil = new ResultUtil<>();
        ResultUtil.setCode(200);
        ResultUtil.setMsg("操作成功");
        ResultUtil.setData(data);
        return ResultUtil;
    }

    /**
     * 成功，无返回数据
     */
    public static <T> ResultUtil<T> success() {
        return success(null);
    }

    /**
     * 成功，自定义消息 + 数据
     */
    public static <T> ResultUtil<T> success(String msg, T data) {
        ResultUtil<T> ResultUtil = new ResultUtil<>();
        ResultUtil.setCode(200);
        ResultUtil.setMsg(msg);
        ResultUtil.setData(data);
        return ResultUtil;
    }

    /**
     * 失败
     */
    public static <T> ResultUtil<T> fail(Integer code, String msg) {
        ResultUtil<T> ResultUtil = new ResultUtil<>();
        ResultUtil.setCode(code);
        ResultUtil.setMsg(msg);
        ResultUtil.setData(null);
        return ResultUtil;
    }

    /**
     * 失败，默认code=500
     */
    public static <T> ResultUtil<T> fail(String msg) {
        return fail(500, msg);
    }

}
