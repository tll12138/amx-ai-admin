package org.dromara.constant;

import org.dromara.common.core.exception.base.ErrorCode;

/**
 * @author zrl
 * @date 2025/7/24
 */
public interface ErrorCodeConstant {

    ErrorCode ERROR_SHARE_TYPE = new ErrorCode(400_000_001, "错误的分享类型");
    ErrorCode ERROR_SHARE_INFO = new ErrorCode(400_000_002, "分享信息格式错误");
    ErrorCode ERROR_SHARE_NOT_EXIST = new ErrorCode(400_000_003, "无效的分享链接");
    ErrorCode INVALIDATE_SHARE_URL = new ErrorCode(400_000_003, "无效的分享链接");

    // 小红书

    ErrorCode XHS_SHARE_EXIST = new ErrorCode(400_001_001, "此链接已经提交过啦，无需再次提交");
}
