package org.dromara.xhs.domain.vo.signature;

import lombok.Data;

/**
 * 获取签名请求
 */
@Data
public class XhsSignatureRequest {
    private String appKey;
    private String nonce;
    private Long timestamp;

    // 构造函数、getter和setter
    public XhsSignatureRequest() {}
}
