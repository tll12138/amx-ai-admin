package org.dromara.ai.utils.dd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author zrl
 * @date 2024/10/31
 */
@Component
@ConfigurationProperties(prefix = "dingtalk")
@Configuration
@Validated
@Data
public class DingTalkProperties {


    private String clientId;

    private String clientSecret;

    private String agentId;

    private String appUrl;

    private String pcUrl;

    private boolean ignoreCheckRedirectUri;

}
