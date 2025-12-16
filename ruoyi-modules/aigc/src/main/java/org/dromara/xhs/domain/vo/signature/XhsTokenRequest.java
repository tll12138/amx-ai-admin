package org.dromara.xhs.domain.vo.signature;

import lombok.Data;

/**
 * 获取token请求
 */
@Data
public class XhsTokenRequest {
    private String nonce;
    private Long timestamp;
    private Integer expiresIn;

    // 构造函数、getter和setter
    public XhsTokenRequest() {}
}
