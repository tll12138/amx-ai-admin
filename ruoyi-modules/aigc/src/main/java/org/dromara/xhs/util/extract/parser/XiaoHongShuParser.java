package org.dromara.xhs.util.extract.parser;

import lombok.extern.slf4j.Slf4j;
import org.dromara.xhs.util.extract.config.ParserConfig;
import org.dromara.xhs.util.extract.core.AbstractLinkParser;
import org.dromara.xhs.util.extract.core.SupportedDomains;
import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;
import org.dromara.xhs.util.extract.model.PlatformEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小红书链接解析器 V2
 * 使用策略模式，简化设计，提高可维护性
 *
 * @author ZRL
 */
@Slf4j
@SupportedDomains({"xiaohongshu.com", "xhslink.com"})
public class XiaoHongShuParser extends AbstractLinkParser {

    // 直链正则模式
    private static final Pattern DIRECT_PATTERN = Pattern.compile("/discovery/item/([0-9a-f]+)");

    // 探索页正则模式
    private static final Pattern EXPLORE_PATTERN = Pattern.compile("/explore/([0-9a-f]+)");

    public XiaoHongShuParser() {
        super();
    }

    public XiaoHongShuParser(ParserConfig config) {
        super(config);
    }


    @Override
    protected LinkParseResult doParse(String url) throws LinkParseException {
        if (config.isDebugEnabled()) {
            log.debug("开始解析小红书链接: {}", url);
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

        // 提取内容ID
        String contentId = extractContentId(actualUrl);
        if (config.isDebugEnabled()) {
            log.debug("提取的内容ID: {}", contentId);
        }

        return new LinkParseResult.Builder()
                .platform(PlatformEnum.XIAO_HONG_SHU)
                .contentId(contentId)
                .linkType(linkType)
                .url(actualUrl)
                .build();
    }

    /**
     * 判断链接类型
     */
    private LinkParseResult.LinkType determineLinkType(String url) {
        return url.contains("xhslink.com") ? LinkParseResult.LinkType.SHARE : LinkParseResult.LinkType.DIRECT;
    }

    /**
     * 提取内容ID
     */
    private String extractContentId(String url) throws LinkParseException {
        // 优先匹配 discovery/item 模式
        Matcher directMatcher = DIRECT_PATTERN.matcher(url);
        if (directMatcher.find()) {
            return directMatcher.group(1);
        }

        // 匹配 explore 模式
        Matcher exploreMatcher = EXPLORE_PATTERN.matcher(url);
        if (exploreMatcher.find()) {
            return exploreMatcher.group(1);
        }

        throw new LinkParseException("无法从URL中提取内容ID: " + url);
    }

    /**
     * 获取解析器名称
     */
    public String getName() {
        return "小红书解析器";
    }

    /**
     * 获取支持的平台
     */
    public PlatformEnum getPlatform() {
        return PlatformEnum.XIAO_HONG_SHU;
    }
}
