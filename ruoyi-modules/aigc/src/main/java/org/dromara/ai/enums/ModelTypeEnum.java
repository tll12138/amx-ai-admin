package org.dromara.ai.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/4/2
 */
@Getter
public enum ModelTypeEnum {

    OPEN_AI("OPEN-AI"),

    WEN_XIN("WEN-XIN"),
    ;

    @JsonValue
    @EnumValue
    private final String model;

    ModelTypeEnum(String model) {
        this.model = model;
    }

    /**
     * 根据model获取枚举
     * @param model 模型值
     * @return 枚举
     */
    public static ModelTypeEnum getByModel(String model) {
        for (ModelTypeEnum value : values()) {
            if (value.getModel().equals(model)) {
                return value;
            }
        }
        return null;
    }
}
