package org.dromara.xhs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小红书配置
 */
@Component
@ConfigurationProperties(prefix = "xhs")
@Data
public class XhsConfig {

    private String appKey;
    private String appSecret;
    private String tokenUrl = "https://edith.xiaohongshu.com/api/sns/v1/ext/access/token";
    private long tokenExpireTime = 7200; // 2小时
    private String transformImageUrl = "https://api.puqi.group/ai/special";
}
