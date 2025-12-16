package org.dromara.xhs.domain.vo.signature;

import lombok.Data;

/**
 * 获取签名响应
 */
@Data
public class XhsSignatureResponse {
    private String appKey;
    private String nonce;
    private Long timestamp;
    private String signature;

    public XhsSignatureResponse() {}

    public XhsSignatureResponse(String appKey, String nonce, Long timestamp, String signature) {
        this.appKey = appKey;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.signature = signature;
    }
}
