package org.dromara.ai.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/5/7
 */
@Getter
public enum SentimentEnum {

    POSITIVE("positive", "生成的所有评论必须都是热情的积极的"),
    NEUTRAL("neutral", "生成的所有评论必须都是中性的，并不是贬低也不是吹捧！"),
    MIXED("mixed", "生成的所有评论既有热情积极的又有中立客观");

    @JsonValue
    @EnumValue
    private final String key;

    private final String mean;

    SentimentEnum(String key, String mean) {
        this.key = key;
        this.mean = mean;
    }
    public static SentimentEnum getByKey(String key) {
        for (SentimentEnum value : SentimentEnum.values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return MIXED;
    }
}
