package org.dromara.ai.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zrl
 * @date 2025/5/7
 */
@Getter
public enum GuidelineEnum {

    QUESTION("question", "生成的所有评论必须都是提问式的，不允许出现其他形式，例如：【'宝宝用了会不会过敏啊？'】"),
    SHARE("share", "生成的所有评论必须都是直述式或经验分享的，绝对不允许出现提问句，例如：【'洗完后皮肤不紧绷，好评！'】"),
    MIXED("mixed", "生成的所有评论即有提问式又有直述式又有经验分享,例如：【'宝宝用了会不会过敏啊？','洗完后皮肤不紧绷，好评！'】"),
    ;

    @JsonValue
    @EnumValue
    private final String key;
    private final String mean;

    GuidelineEnum(String key, String mean) {
        this.key = key;
        this.mean = mean;
    }
    public static GuidelineEnum getByKey(String key) {
        for (GuidelineEnum value : values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return MIXED;
    }
}
