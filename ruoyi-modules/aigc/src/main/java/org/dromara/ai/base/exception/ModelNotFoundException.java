package org.dromara.ai.base.exception;

public class ModelNotFoundException extends RuntimeException {
    public ModelNotFoundException(String modelId) {
        super("AI模型配置不存在: " + modelId);
    }
}
