package org.dromara.xhs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 小红书分享配置属性
 *
 * @author ruoyi
 * @date 2024-01-01
 */
@Data
@Component
@ConfigurationProperties(prefix = "xhs.share")
public class XhsShareProperties {

    /**
     * 分享链接前缀
     */
    private String urlPrefix;

    /**
     * Redis缓存前缀
     */
    private String cachePrefix = "xhs:share:";

    /**
     * 默认过期时间（天）
     */
    private Integer defaultExpireDays = 30;

    /**
     * 二维码配置
     */
    private QrCode qrcode = new QrCode();

    @Data
    public static class QrCode {
        /**
         * 二维码宽度
         */
        private Integer width = 300;

        /**
         * 二维码高度
         */
        private Integer height = 300;

        /**
         * 二维码边距
         */
        private Integer margin = 1;
    }
}
