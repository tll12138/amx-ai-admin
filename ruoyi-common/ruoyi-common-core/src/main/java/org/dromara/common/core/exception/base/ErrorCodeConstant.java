package org.dromara.common.core.exception.base;

/**
 * @author zrl
 * @date 2025/7/24
 */
public interface ErrorCodeConstant {

    ErrorCode SYSTEM_ERROR = new ErrorCode(500, "系统内部异常");
    ErrorCode PARAMS_ERROR = new ErrorCode(500_001, "参数异常");
}
