package org.dromara.xhs.util.extract.model;

import org.apache.commons.lang3.StringUtils;
import org.dromara.xhs.util.extract.core.Parser;
import org.dromara.xhs.util.extract.registry.ParserRegistry;

import java.util.Set;

/**
 * 平台类型枚举
 */
public enum PlatformEnum {
    XIAO_HONG_SHU,
    DOU_YIN,
    UNSUPPORTED;

    /**
     * 根据URL识别平台类型
     * 优先通过解析器的@SupportedDomains注解进行匹配
     * 如果没有匹配的解析器，则使用默认的字符串匹配方式
     */
    public static PlatformEnum identify(String url) {
        if (StringUtils.isBlank(url)) {
            return UNSUPPORTED;
        }

        // 1. 优先通过解析器注解匹配
        Set<PlatformEnum> supportedPlatforms = ParserRegistry.getSupportedPlatforms();
        for (PlatformEnum platformEnum : supportedPlatforms) {
            Parser parser = ParserRegistry.getParser(platformEnum);
            if (parser != null && parser.supports(url)) {
                return platformEnum;
            }
        }

        // 2. 如果没有匹配的解析器，使用默认的字符串匹配方式
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("xiaohongshu")) {
            return XIAO_HONG_SHU;
        }
        if (lowerUrl.contains("douyin")) {
            return DOU_YIN;
        }

        return UNSUPPORTED;
    }
}
