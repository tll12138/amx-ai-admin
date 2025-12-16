package org.dromara.ai.base.vo;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AIChunkResponse {

    private String content;    // 当前块内容
    private boolean isThinking; // 是否处于思考中
    private boolean isEnd; // 原始元数据（用于调试）

    public AIChunkResponse(String content) {
        this.content = content;
        this.isThinking = false;
        this.isEnd = false;
    }

    public static String endStream() {
        return AIChunkResponse.builder()
            .content("")
            .isThinking(false)
            .isEnd(true)
            .build().
            toJsonString();
    }

    public static String empty() {
        return AIChunkResponse.builder()
            .content("")
            .isThinking(false)
            .isEnd(false)
            .build().
            toJsonString();
    }

    public static String startThink() {
        return AIChunkResponse.builder()
            .content("")
            .isThinking(true)
            .isEnd(false)
            .build()
            .toJsonString();
    }

    public static String endThink() {
        return AIChunkResponse.builder()
            .content("")
            .isThinking(false)
            .isEnd(false)
            .build()
            .toJsonString();
    }

    public static String thinking(String content) {
        return AIChunkResponse.builder()
            .content(content)
            .isThinking(true)
            .isEnd(false)
            .build()
            .toJsonString();
    }
    public static String normalOutput(String content) {
        return AIChunkResponse.builder()
            .content(content)
            .isThinking(false)
            .isEnd(false)
            .build()
            .toJsonString();
    }

    public static String chatOutput(String content) {
        return AIChunkResponse.builder()
            .content(content)
            .isThinking(false)
            .isEnd(true)
            .build()
            .toJsonString();
    }


    // 转换为JSON字符串
    public String toJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}
