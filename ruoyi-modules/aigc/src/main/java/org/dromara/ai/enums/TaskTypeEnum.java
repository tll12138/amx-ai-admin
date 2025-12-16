package org.dromara.ai.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/3/25
 */
@Getter
public enum TaskTypeEnum {

    HENG_CE("hc"),
    ZHONG_CAO("zc"),
    OTHER("other"),
    ;

    @JsonValue
    @EnumValue
    private final String type;

    TaskTypeEnum(String type) {
        this.type = type;
    }
    public static TaskTypeEnum getByType(String type) {
        for (TaskTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return OTHER;
    }
}
