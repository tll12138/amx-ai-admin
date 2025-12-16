package org.dromara.ai.base.vo;

import lombok.*;
import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.domain.AiModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Component
public class OpenAIModelConfig extends ModelConfig {

    private String model;

    private String apiKey;

    private String baseUrl;

    private String endpoint;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    @Override
    public String getProvider() {
        return ModelProvider.OPENAI.getProvider();
    }

    @Override
    public Map<String, Object> getDefaultParams() {
        return Map.of(
            "model", this.model,
            "temperature", this.temperature,
            "max_tokens", this.maxTokens,
            "top_p", this.topP
        );
    }

    @Override
    public OpenAIModelConfig formAiModel(AiModel aiModel) {
        return OpenAIModelConfig.builder()
            .model(aiModel.getModel())
            .apiKey(aiModel.getApiKey())
            .baseUrl(aiModel.getBaseUrl())
            .endpoint(aiModel.getEndpoint())
            .temperature(aiModel.getTemperature())
            .maxTokens(aiModel.getMaxToken())
            .topP(aiModel.getTopP())
            .build();
    }
}
