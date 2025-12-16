package org.dromara.ai.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/3/25
 */
@Getter
public enum ProductType {

    PRODUCT("product"),
    COMPETITOR("competitor"),
    ;

    @JsonValue
    @EnumValue
    private final String type;

    ProductType(String type) {
        this.type = type;
    }
}
