package org.dromara.xhs.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * {@code @date} 2025/7/24
 */

@Getter
public enum ShareTypeEnum {

    NORMAL( "normal", "图文"),

    VIDEO("video","视频")
    ;

    @JsonValue
    @EnumValue
    private final String type;

    private final String message;

    public static final String NORMAL_TYPE = NORMAL.type;
    public static final String VIDEO_TYPE = VIDEO.type;


    ShareTypeEnum(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public static ShareTypeEnum getShareType(String type) {
        for (ShareTypeEnum value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }
}
