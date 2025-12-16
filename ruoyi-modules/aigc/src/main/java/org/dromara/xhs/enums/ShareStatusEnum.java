package org.dromara.xhs.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/7/24
 */

@Getter
public enum ShareStatusEnum {

    DELETED(0, "删除"),

    NORMAL(1, "正常"),

    EXPIRED(2, "过期");

    @JsonValue
    @EnumValue
    private final Integer code;

    private final String message;

    public  static final Integer DELETED_CODE = DELETED.code;
    public  static final Integer NORMAL_CODE = NORMAL.code;
    public  static final Integer EXPIRED_CODE = EXPIRED.code;

    ShareStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
