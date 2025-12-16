package org.dromara.xhs.util.extract.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;
import org.dromara.xhs.util.extract.model.PlatformEnum;
import org.dromara.xhs.util.extract.registry.ParserRegistry;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.dromara.common.core.utils.ServiceExceptionUtil.exception;
import static org.dromara.constant.ErrorCodeConstant.INVALIDATE_SHARE_URL;

/**
 * 链接提取器 - 统一入口类
 * 支持单个解析器和链式解析器两种使用方式
 *
 * 使用示例：
 * 1. 自动选择解析器：LinkExtractor.defaultChain().parse(url)
 * 2. 指定解析器：LinkExtractor.with(PlatformEnum.XIAO_HONG_SHU).parse(url)
 * 3. 链式解析：LinkExtractor.chain().add(parser1).add(parser2).parse(url)
 *
 * @author ZRL
 */
@Slf4j
public class LinkExtractor {

    private final List<Parser> parsers;

    private static final String HTTP_URL_Pattern =  "https?://[\\w\\-./?%&=]*";

    private LinkExtractor(List<Parser> parsers) {
        this.parsers = new ArrayList<>(parsers);
    }

    /**
     * 指定平台类型进行解析
     */
    public static LinkParseResult parse(String url, PlatformEnum platformEnum) throws LinkParseException {
        Parser parser = ParserRegistry.getParser(platformEnum);
        if (parser == null) {
            throw new LinkParseException("不支持的平台类型: " + platformEnum);
        }
        return with(parser).parse(url);
    }

    /**
     * 使用指定解析器
     */
    public static LinkExtractor with(Parser parser) {
        return new LinkExtractor(Collections.singletonList(parser));
    }

    /**
     * 使用指定平台类型的解析器
     */
    public static LinkExtractor with(PlatformEnum platformEnum) {
        Parser parser = ParserRegistry.getParser(platformEnum);
        if (parser == null) {
            throw new IllegalArgumentException("不支持的平台类型: " + platformEnum);
        }
        return new LinkExtractor(Collections.singletonList(parser));
    }

    /**
     * 创建解析器链建造者
     */
    public static ChainBuilder chain() {
        return new ChainBuilder();
    }

    /**
     * 获取默认解析器链
     */
    public static LinkExtractor defaultChain() {
        return new LinkExtractor(ParserRegistry.getAllParsers());
    }

    /**
     * 执行解析
     */
    public LinkParseResult parse(String url) throws LinkParseException {
        String processedUrl = preprocessUrl(url);
        // 处理链接 匹配出http 或者 https 部分

        for (Parser parser : parsers) {
            if (parser.supports(processedUrl)) {
                try {
                    LinkParseResult result = parser.parse(processedUrl);
                    log.info("使用解析器 {} 成功解析链接: {}", parser.getClass().getSimpleName(), url);
                    return result;
                } catch (Exception e) {
                    log.warn("解析器 {} 解析失败: {}", parser.getClass().getSimpleName(), e.getMessage());
                    // 继续尝试下一个解析器
                }
            }
        }

        throw new LinkParseException("找不到合适的解析器处理链接: " + url);
    }

    /**
     * 尝试解析，返回Optional结果
     */
    public Optional<LinkParseResult> tryParse(String url) {
        try {
            return Optional.of(parse(url));
        } catch (LinkParseException e) {
            log.debug("解析失败: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * URL预处理
     */
    private String preprocessUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("URL不能为空");
        }
        // 编译正则表达式
        Pattern pattern = Pattern.compile(HTTP_URL_Pattern);
        Matcher matcher = pattern.matcher(url);

        // 查找第一个匹配的URL
        if (!matcher.find()) {
            throw exception(INVALIDATE_SHARE_URL, "无效的链接");
        }
        return matcher.group();
    }

    /**
     * 解析器链建造者
     */
    public static class ChainBuilder {
        private final List<Parser> parsers = new ArrayList<>();

        /**
         * 添加解析器
         */
        public ChainBuilder add(Parser parser) {
            if (parser != null) {
                parsers.add(parser);
            }
            return this;
        }

        /**
         * 添加指定平台的解析器
         */
        public ChainBuilder add(PlatformEnum platformEnum) {
            Parser parser = ParserRegistry.getParser(platformEnum);
            if (parser != null) {
                parsers.add(parser);
            }
            return this;
        }

        /**
         * 添加多个解析器
         */
        public ChainBuilder addAll(Parser... parsers) {
            for (Parser parser : parsers) {
                add(parser);
            }
            return this;
        }

        /**
         * 添加多个平台的解析器
         */
        public ChainBuilder addAll(PlatformEnum... platformEnums) {
            for (PlatformEnum platformEnum : platformEnums) {
                add(platformEnum);
            }
            return this;
        }

        /**
         * 构建解析器链
         */
        public LinkExtractor build() {
            if (parsers.isEmpty()) {
                throw new IllegalStateException("解析器链不能为空");
            }
            return new LinkExtractor(parsers);
        }

        /**
         * 构建并解析
         */
        public LinkParseResult parse(String url) throws LinkParseException {
            return build().parse(url);
        }

        /**
         * 构建并尝试解析
         */
        public Optional<LinkParseResult> tryParse(String url) {
            return build().tryParse(url);
        }
    }
}
