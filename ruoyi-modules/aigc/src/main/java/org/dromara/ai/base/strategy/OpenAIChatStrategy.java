package org.dromara.ai.base.strategy;

import lombok.AllArgsConstructor;
import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.domain.AiModel;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static org.dromara.ai.base.enums.ModelProvider.*;


@Service
@AllArgsConstructor
public class OpenAIChatStrategy extends AbstractChatStrategy {
    @Override
    public Set<ModelProvider> getSupportedProviders() {
        return Set.of(OPENAI, Q_WEN, DEEP_SEEK, Q_FAN);
    }

    @Override
    protected Map<String, Object> buildRequestBody(AiModel model, Map<String, Object> prompt) {
        return super.buildRequestBody(model, prompt);
    }
}
