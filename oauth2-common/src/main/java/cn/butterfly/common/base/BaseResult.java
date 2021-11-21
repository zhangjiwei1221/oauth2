package cn.butterfly.common.base;

import cn.butterfly.common.constant.BaseConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static cn.butterfly.common.constant.BaseConstants.ERROR;
import static cn.butterfly.common.constant.BaseConstants.SUCCESS;

/**
 * 控制器返回结果基本类
 *
 * @author zjw
 * @date 2021-09-12
 */
@Data
@AllArgsConstructor
public class BaseResult<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 返回成功
     *
     * @return 结果
     */
    public static <T> BaseResult<T> success() {
        return success(null);
    }

    /**
     * 返回成功
     *
     * @param data 返回数据
     * @return 结果
     */
    public static <T> BaseResult<T> success(T data) {
        return success(null, data);
    }

    /**
     * 返回成功
     *
     * @param msg 成功信息
     * @param data 返回数据
     * @return 结果
     */
    public static <T> BaseResult<T> success(String msg, T data) {
        return get(SUCCESS, msg, data);
    }

    /**
     * 返回错误
     *
     * @param msg 错误信息
     * @return 结果
     */
    public static <T> BaseResult<T> error(String msg) {
        return error(ERROR, msg);
    }

    /**
     * 返回错误
     *
     * @param code 错误码
     * @param msg 错误信息
     * @return 结果
     */
    public static <T> BaseResult<T> error(int code, String msg) {
        return get(code, msg, null);
    }


    /**
     * 获取返回信息实体
     *
	 * @param code 状态码
	 * @param msg 信息
	 * @param data 数据
     * @return 结果
     */
    private static <T> BaseResult<T> get(int code, String msg, T data) {
        BaseResult<T> result = new BaseResult<>();
        result.setCode(code);
        result.setMessage(StringUtils.defaultString(msg, BaseConstants.DEFAULT_MESSAGE));
        result.setData(data);
        return result;
    }

    private BaseResult() {}

}
