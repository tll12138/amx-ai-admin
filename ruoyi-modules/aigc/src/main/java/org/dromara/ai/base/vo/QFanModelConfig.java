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
@EqualsAndHashCode(callSuper = false)
@Component // 添加 @Component 注解
public class QFanModelConfig extends ModelConfig {

    private String model;

    private String apiKey;

    private String apiSecret;

    private String baseUrl;

    private String endpoint;

    private Double temperature;

    private Integer maxTokens;

    private Double topP;

    @Override
    public String getProvider() {
        return ModelProvider.Q_FAN.getProvider();
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
    public QFanModelConfig formAiModel(AiModel aiModel) {
        return QFanModelConfig.builder()
            .model(aiModel.getModel())
            .apiKey(aiModel.getApiKey())
            .apiSecret(aiModel.getSecretKey())
            .baseUrl(aiModel.getBaseUrl())
            .endpoint(aiModel.getEndpoint())
            .temperature(aiModel.getTemperature())
            .maxTokens(aiModel.getMaxToken())
            .topP(aiModel.getTopP())
            .build();
    }
}
