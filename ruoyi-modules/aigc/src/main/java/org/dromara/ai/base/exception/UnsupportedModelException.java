package org.dromara.ai.base.exception;

public class UnsupportedModelException extends RuntimeException {
    public UnsupportedModelException(String provider) {
        super("不支持的AI供应商: " + provider);
    }
}
