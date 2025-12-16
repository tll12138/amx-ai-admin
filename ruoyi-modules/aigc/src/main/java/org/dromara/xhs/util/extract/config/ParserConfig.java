package org.dromara.xhs.util.extract.config;

import lombok.Builder;
import lombok.Data;

/**
 * 解析器配置类
 * 用于配置解析器的各种参数
 *
 * @author ZRL
 */
@Data
@Builder
public class ParserConfig {

    /**
     * HTTP请求超时时间（毫秒）
     */
    @Builder.Default
    private int timeoutMs = 5000;

    /**
     * User-Agent
     */
    @Builder.Default
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    /**
     * 是否启用重试机制
     */
    @Builder.Default
    private boolean retryEnabled = true;

    /**
     * 重试次数
     */
    @Builder.Default
    private int maxRetries = 3;

    /**
     * 重试间隔（毫秒）
     */
    @Builder.Default
    private int retryIntervalMs = 1000;

    /**
     * 是否启用缓存
     */
    @Builder.Default
    private boolean cacheEnabled = true;

    /**
     * 缓存过期时间（毫秒）
     */
    @Builder.Default
    private long cacheExpireMs = 300000; // 5分钟

    /**
     * 是否启用调试日志
     */
    @Builder.Default
    private boolean debugEnabled = false;

    /**
     * 是否严格模式（严格验证URL格式）
     */
    @Builder.Default
    private boolean strictMode = false;

    /**
     * 获取默认配置
     */
    public static ParserConfig getDefault() {
        return ParserConfig.builder().build();
    }

    /**
     * 获取开发环境配置
     */
    public static ParserConfig getDevelopment() {
        return ParserConfig.builder()
                .debugEnabled(true)
                .timeoutMs(10000)
                .strictMode(false)
                .build();
    }

    /**
     * 获取生产环境配置
     */
    public static ParserConfig getProduction() {
        return ParserConfig.builder()
                .debugEnabled(false)
                .timeoutMs(3000)
                .strictMode(true)
                .maxRetries(2)
                .build();
    }

    /**
     * 获取高性能配置
     */
    public static ParserConfig getHighPerformance() {
        return ParserConfig.builder()
                .timeoutMs(2000)
                .retryEnabled(false)
                .cacheEnabled(true)
                .debugEnabled(false)
                .build();
    }
}
