package org.dromara.xhs.util.extract.parser;

import lombok.extern.slf4j.Slf4j;
import org.dromara.xhs.util.extract.config.ParserConfig;
import org.dromara.xhs.util.extract.core.AbstractLinkParser;
import org.dromara.xhs.util.extract.core.SupportedDomains;
import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;
import org.dromara.xhs.util.extract.model.PlatformEnum;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抖音链接解析器
 * 使用策略模式，简化设计，提高可维护性
 *
 * @author ZRL
 */
@Slf4j
@SupportedDomains({"v.douyin.com", "iesdouyin.com"})
public class DouYinParser extends AbstractLinkParser {

    public DouYinParser() {
        super();
    }

    public DouYinParser(ParserConfig config) {
        super(config);
    }

    // 增强版正则匹配模式
    private static final Pattern VIDEO_PATTERN = Pattern.compile("/video/(\\d+)(?:/|\\?)?");
    private static final Pattern DISCOVER_PATTERN = Pattern.compile("/discover\\?modal_id=(\\d+)");
    private static final Pattern USER_PATTERN = Pattern.compile("/user/[^/]+\\?.*modal_id=(\\d+)");



    @Override
    protected LinkParseResult doParse(String url) throws LinkParseException {
        if (config.isDebugEnabled()) {
            log.debug("开始解析抖音链接: {}", url);
        }

        // 判断链接类型
        LinkParseResult.LinkType linkType = determineLinkType(url);
        if (config.isDebugEnabled()) {
            log.debug("链接类型: {}", linkType);
        }

        // 处理短链接跳转
        String actualUrl = url;
        if (linkType == LinkParseResult.LinkType.SHARE) {
            actualUrl = resolveShortLink(url);
            if (config.isDebugEnabled()) {
                log.debug("解析后的实际链接: {}", actualUrl);
            }
        }

        // 提取视频ID
        String videoId = extractVideoId(actualUrl);
        if (config.isDebugEnabled()) {
            log.debug("提取的视频ID: {}", videoId);
        }

        return new LinkParseResult.Builder()
                .platform(PlatformEnum.DOU_YIN)
                .contentId(videoId)
                .linkType(linkType)
                .url(actualUrl)
                .build();
    }

    /**
     * 判断链接类型
     */
    private LinkParseResult.LinkType determineLinkType(String url) {
        return url.contains("v.douyin.com") ? LinkParseResult.LinkType.SHARE : LinkParseResult.LinkType.DIRECT;
    }



    /**
     * 提取视频ID
     */
    private String extractVideoId(String url) throws LinkParseException {
        // URL解码处理
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

        // 尝试多种匹配模式
        Pattern[] patterns = {VIDEO_PATTERN, DISCOVER_PATTERN, USER_PATTERN};

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(decodedUrl);
            if (matcher.find()) {
                String videoId = matcher.group(1);
                log.debug("使用模式 {} 提取到视频ID: {}", pattern.pattern(), videoId);
                return videoId;
            }
        }

        throw new LinkParseException("无法从URL中提取视频ID: " + url);

    }

    /**
     * 获取解析器名称
     */
    public String getName() {
        return "抖音解析器";
    }

    /**
     * 获取支持的平台
     */
    public PlatformEnum getPlatform() {
        return PlatformEnum.DOU_YIN;
    }
}
