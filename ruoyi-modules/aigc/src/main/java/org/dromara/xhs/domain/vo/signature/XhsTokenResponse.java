package org.dromara.xhs.domain.vo.signature;

import lombok.Data;
import org.dromara.xhs.domain.XhsTokenResponseData;

/**
 * 获取token响应
 */
@Data
public class XhsTokenResponse {

    private Integer code;
    private Boolean success;
    private String msg;
    private XhsTokenResponseData data;
}
