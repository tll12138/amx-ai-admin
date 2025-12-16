package org.dromara.xhs.util.extract.exception;

/**
 * 自定义解析异常
 */
public class LinkParseException extends Exception {
    public LinkParseException(String message) {
        super(message);
    }

    public LinkParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
