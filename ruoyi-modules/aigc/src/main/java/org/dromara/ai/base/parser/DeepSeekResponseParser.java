package org.dromara.ai.base.parser;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.dromara.ai.base.constants.AIConstants.AI_STREAM_RESPONSE;
import static org.dromara.ai.base.enums.ModelProvider.*;

@Component
@Slf4j
public class DeepSeekResponseParser implements ResponseParser {

    @Override
    public Set<ModelProvider> getSupportedProviders() {
        return Set.of(Q_WEN, DEEP_SEEK, Q_FAN);
    }

    @Override
    public String parseResponse(String raw) {
        try {

            // 1. 提取有效JSON部分
            if ("[DONE]".equals(raw.trim())) return AIChunkResponse.endStream();

            // 2. 解析JSON
            JsonNode root = mapper.readTree(raw);

            // 3.检查是否报错
            if (root.has("error")) {
                throw new RuntimeException(root.path("error").path("message").asText());
            }

            // 4. 检查是否停止
            boolean flag = root.path("choices").get(0).has("finish_reason");
            if (flag){
                String finish_reason = root.path("choices").get(0).get("finish_reason").asText("");
                if (StrUtil.isNotEmpty(finish_reason) && !"stop".equals(finish_reason)){
                    throw new RuntimeException(finish_reason);
                }
            }

            // 5. 判断是chat响应还是stream响应
            if (AI_STREAM_RESPONSE.equals(root.path("object").asText(""))) {
                // 5.1 提取Stream内容
                JsonNode delta = root.path("choices").get(0).get("delta");
                String content = delta.path("content").asText("");
                String reasoningContent = delta.path("reasoning_content").asText("");
                if (StrUtil.isNotEmpty(reasoningContent)) {
                    return AIChunkResponse.thinking(reasoningContent);
                }
                return AIChunkResponse.normalOutput(content);
            } else {
                // 5.1 提取消息内容
                log.info("消息内容：{}", raw);
                JsonNode message = root.path("choices").get(0).get("message");
                String content = message.path("content").asText("");
                return AIChunkResponse.chatOutput(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
