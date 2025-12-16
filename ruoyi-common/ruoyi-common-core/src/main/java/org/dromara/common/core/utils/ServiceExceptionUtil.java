package org.dromara.common.core.utils;

import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.exception.base.ErrorCode;

/**
 * {@link ServiceException} 工具类
 *
 * 目的在于，格式化异常信息提示。
 *
 */
public class ServiceExceptionUtil {

    // ========== 和 ServiceException 的集成 ==========

    public static ServiceException exception(ErrorCode errorCode) {
        return exception0(errorCode.getCode(), errorCode.getMsg());
    }

    public static ServiceException exception(ErrorCode errorCode, String message) {
        return exception0(errorCode.getCode(), message);
    }


    public static ServiceException exception0(Integer code, String message) {
        return new ServiceException(message, code);
    }

}
