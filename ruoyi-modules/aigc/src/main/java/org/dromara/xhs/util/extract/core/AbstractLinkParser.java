package org.dromara.xhs.util.extract.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.xhs.util.extract.config.ParserConfig;
import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 抽象解析器基类
 * 提供通用功能：缓存、重试、配置管理、HTTP请求等
 *
 * @author ZRL
 */
@Slf4j
public abstract class AbstractLinkParser implements Parser {

    /**
     * 解析结果缓存
     */
    private static final ConcurrentMap<String, CacheEntry> CACHE = new ConcurrentHashMap<>();

    /**
     * 解析器配置
     */
    protected ParserConfig config;

    /**
     * 默认构造函数，使用默认配置
     */
    public AbstractLinkParser() {
        this(ParserConfig.getDefault());
    }

    /**
     * 带配置的构造函数
     */
    public AbstractLinkParser(ParserConfig config) {
        this.config = config;
    }

    @Override
    public boolean supports(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }

        // 获取类上的@SupportedDomains注解
        SupportedDomains annotation = this.getClass().getAnnotation(SupportedDomains.class);
        if (annotation == null) {
            // 如果没有注解，子类需要自己实现supports方法
            throw new UnsupportedOperationException(
                "解析器 " + this.getClass().getSimpleName() + " 必须使用@SupportedDomains注解或重写supports方法");
        }

        String[] supportedDomains = annotation.value();
        boolean strict = annotation.strict();

        if (strict) {
            // 严格模式：检查完整域名匹配
            return isStrictDomainMatch(url, supportedDomains);
        } else {
            // 非严格模式：检查URL是否包含域名字符串
            String lowerUrl = url.toLowerCase();
            for (String domain : supportedDomains) {
                if (lowerUrl.contains(domain.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 严格域名匹配检查
     */
    private boolean isStrictDomainMatch(String url, String[] supportedDomains) {
        try {
            URL urlObj = new URL(url);
            String host = urlObj.getHost().toLowerCase();

            for (String domain : supportedDomains) {
                String lowerDomain = domain.toLowerCase();
                if (host.equals(lowerDomain) || host.endsWith("." + lowerDomain)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            // URL格式错误时回退到非严格模式
            String lowerUrl = url.toLowerCase();
            for (String domain : supportedDomains) {
                if (lowerUrl.contains(domain.toLowerCase())) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public final LinkParseResult parse(String url) throws LinkParseException {
        if (StringUtils.isBlank(url)) {
            throw new LinkParseException("URL不能为空");
        }

        String normalizedUrl = normalizeUrl(url);

        // 检查缓存
        if (config.isCacheEnabled()) {
            LinkParseResult cachedResult = getFromCache(normalizedUrl);
            if (cachedResult != null) {
                if (config.isDebugEnabled()) {
                    log.debug("从缓存获取解析结果: {}", normalizedUrl);
                }
                return cachedResult;
            }
        }

        // 执行解析
        LinkParseResult result = executeParseWithRetry(normalizedUrl);

        // 缓存结果
        if (config.isCacheEnabled() && result != null) {
            putToCache(normalizedUrl, result);
        }

        return result;
    }

    /**
     * 带重试机制的解析执行
     */
    private LinkParseResult executeParseWithRetry(String url) throws LinkParseException {
        LinkParseException lastException = null;

        int maxAttempts = config.isRetryEnabled() ? config.getMaxRetries() + 1 : 1;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                if (config.isDebugEnabled()) {
                    log.debug("第{}次尝试解析: {}", attempt, url);
                }

                return doParse(url);

            } catch (LinkParseException e) {
                lastException = e;

                if (attempt < maxAttempts) {
                    if (config.isDebugEnabled()) {
                        log.debug("第{}次解析失败，准备重试: {}", attempt, e.getMessage());
                    }

                    // 等待重试间隔
                    try {
                        Thread.sleep(config.getRetryIntervalMs());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new LinkParseException("解析被中断", ie);
                    }
                } else {
                    if (config.isDebugEnabled()) {
                        log.debug("所有重试都失败了");
                    }
                }
            }
        }

        throw lastException;
    }

    /**
     * 具体的解析实现，由子类实现
     */
    protected abstract LinkParseResult doParse(String url) throws LinkParseException;

    /**
     * URL标准化
     */
    protected String normalizeUrl(String url) {
        return url.trim();
    }

    /**
     * 解析短链接
     */
    protected String resolveShortLink(String url) throws LinkParseException {
        try {
            HttpRequest request = createHttpRequest(url);
            request.setFollowRedirects(false); // 禁用自动重定向

            HttpResponse response = request.execute();

            // 检查重定向状态码
            int status = response.getStatus();
            if (status == 301 || status == 302 || status == 307 || status == 308) {
                String location = response.header(Header.LOCATION);
                if (location != null && !location.isEmpty()) {
                    if (config.isDebugEnabled()) {
                        log.debug("短链接重定向: {} -> {}", url, location);
                    }
                    return location;
                }
            }

            // 如果没有重定向，返回原URL
            return url;

        } catch (Exception e) {
            log.warn("解析短链接失败: {}", url, e);
            throw new LinkParseException("解析短链接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建HTTP请求
     */
    protected HttpRequest createHttpRequest(String url) {
        HttpRequest request = HttpUtil.createGet(url);
        request.header(Header.USER_AGENT, config.getUserAgent());
        request.timeout(config.getTimeoutMs());
        return request;
    }

    /**
     * 从缓存获取结果
     */
    private LinkParseResult getFromCache(String url) {
        CacheEntry entry = CACHE.get(url);
        if (entry != null && !entry.isExpired()) {
            return entry.getResult();
        }

        // 清理过期缓存
        if (entry != null) {
            CACHE.remove(url);
        }

        return null;
    }

    /**
     * 将结果放入缓存
     */
    private void putToCache(String url, LinkParseResult result) {
        long expireTime = System.currentTimeMillis() + config.getCacheExpireMs();
        CACHE.put(url, new CacheEntry(result, expireTime));
    }

    /**
     * 清理缓存
     */
    public static void clearCache() {
        CACHE.clear();
        log.info("解析器缓存已清理");
    }

    /**
     * 获取缓存统计信息
     */
    public static CacheStats getCacheStats() {
        long currentTime = System.currentTimeMillis();
        long validEntries = CACHE.values().stream()
                .mapToLong(entry -> entry.isExpired(currentTime) ? 0 : 1)
                .sum();

        return new CacheStats(CACHE.size(), validEntries);
    }

    /**
     * 缓存条目
     */
    private static class CacheEntry {
        private final LinkParseResult result;
        private final long expireTime;

        public CacheEntry(LinkParseResult result, long expireTime) {
            this.result = result;
            this.expireTime = expireTime;
        }

        public LinkParseResult getResult() {
            return result;
        }

        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        public boolean isExpired(long currentTime) {
            return currentTime > expireTime;
        }
    }

    /**
     * 缓存统计信息
     */
    public static class CacheStats {
        private final long totalEntries;
        private final long validEntries;

        public CacheStats(long totalEntries, long validEntries) {
            this.totalEntries = totalEntries;
            this.validEntries = validEntries;
        }

        public long getTotalEntries() {
            return totalEntries;
        }

        public long getValidEntries() {
            return validEntries;
        }

        public long getExpiredEntries() {
            return totalEntries - validEntries;
        }

        @Override
        public String toString() {
            return String.format("CacheStats{total=%d, valid=%d, expired=%d}",
                    totalEntries, validEntries, getExpiredEntries());
        }
    }
}
