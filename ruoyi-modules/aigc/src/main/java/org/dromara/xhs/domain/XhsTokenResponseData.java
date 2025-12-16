package org.dromara.xhs.domain;

import lombok.Data;

@Data
public class XhsTokenResponseData {
    private String accessToken;
    private Long expiresIn;

    public XhsTokenResponseData(String accessToken, Long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
