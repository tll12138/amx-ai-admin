package org.dromara.ai.base.strategy;

import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.domain.AiModel;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;

public interface ChatStrategy {
    Set<ModelProvider> getSupportedProviders(); // 获取平台名称
    Flux<String> streamGenerate(AiModel model, Map<String, Object> prompt);
    String chatGenerate(AiModel model, Map<String, Object> prompt);
}
