package org.dromara.ai.base.factory;

import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.vo.ModelConfig;
import org.dromara.ai.domain.AiModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ModelFactory {

    private final Map<ModelProvider, ModelConfig> modelConfigs = new ConcurrentHashMap<>();

    @Autowired
    public ModelFactory(List<ModelConfig> modelConfigList) {
        for (ModelConfig config : modelConfigList) {
            modelConfigs.put(ModelProvider.valueOf(config.getProvider().toUpperCase()), config);
        }
    }

    public Map<String, Object> getModelConfig(AiModel aiModel) {
        ModelProvider provider = ModelProvider.getByProvider(aiModel.getProvider().toUpperCase());
        ModelConfig modelConfig = modelConfigs.getOrDefault(provider, modelConfigs.get(ModelProvider.OPENAI));
        return modelConfig.formAiModel(aiModel).getDefaultParams();
    }
}
