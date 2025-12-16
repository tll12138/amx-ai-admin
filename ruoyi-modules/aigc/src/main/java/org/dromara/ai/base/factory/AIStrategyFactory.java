package org.dromara.ai.base.factory;

import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.strategy.ChatStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AIStrategyFactory {

    private final Map<ModelProvider, ChatStrategy> strategies = new ConcurrentHashMap<>();

    @Autowired
    public AIStrategyFactory(List<ChatStrategy> strategyList) {
        for (ChatStrategy strategy : strategyList) {
            for (ModelProvider provider : strategy.getSupportedProviders()) {
                if (!strategies.containsKey(provider)) {
                    strategies.put(provider, strategy);
                }
            }
        }
    }

    public ChatStrategy getStrategy(ModelProvider provider) {
        return
            strategies.getOrDefault(
                provider,
                strategies.get(ModelProvider.OPENAI)
            ); // 默认回退到 OpenAI
    }
}
