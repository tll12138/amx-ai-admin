package org.dromara.ai.base.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ModelProvider {
    OPENAI("openai"),
    Q_FAN("q_fan"),
    Q_WEN("q_wen"),
    DEEP_SEEK("deepseek")
    ;


    @EnumValue
    @JsonValue
    private final String provider;

    ModelProvider(String provider) {
        this.provider = provider;
    }

    public static ModelProvider getByProvider(String provider) {
        for (ModelProvider value : values()) {
            if (value.provider.equals(provider)) {
                return value;
            }
        }
        return OPENAI;
    }
}
