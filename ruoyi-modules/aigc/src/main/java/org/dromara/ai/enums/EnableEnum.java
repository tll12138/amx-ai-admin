package org.dromara.ai.enums;

import lombok.Getter;

/**
 * @author zrl
 * @date 2025/4/8
 */
@Getter
public enum EnableEnum {

    ON("on"),
    OFF("off");

    private final String value;

    EnableEnum(String value) {
        this.value = value;
    }
}
