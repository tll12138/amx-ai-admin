package org.dromara.xhs.util.extract.core;

import org.dromara.xhs.util.extract.exception.LinkParseException;
import org.dromara.xhs.util.extract.model.LinkParseResult;

/**
 * 解析器接口
 */
public interface Parser {
    /**
     * 判断是否支持当前链接
     */
    boolean supports(String url);

    /**
     * 执行链接解析
     */
    LinkParseResult parse(String url) throws LinkParseException;
}
