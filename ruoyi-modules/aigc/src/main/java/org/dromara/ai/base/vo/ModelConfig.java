package org.dromara.ai.base.vo;

import org.dromara.ai.domain.AiModel;

import java.util.Map;

public abstract class ModelConfig {

    public abstract String getProvider();

    public abstract Map<String, Object> getDefaultParams();

    public abstract ModelConfig formAiModel(AiModel aiModel);
}
