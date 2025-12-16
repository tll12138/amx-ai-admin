package org.dromara.ai.base.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.dromara.ai.base.enums.ModelProvider.OPENAI;

@Component
public class OpenAIResponseParser implements ResponseParser {

    @Override
    public Set<ModelProvider> getSupportedProviders() {
        return Set.of(OPENAI);
    }

    @Override
    public String parseResponse(String rawChunk) {
        try {
            // 1. 提取有效JSON部分
            if ("[DONE]".equals(rawChunk.trim())) return AIChunkResponse.endStream();

            // 2. 解析JSON
            JsonNode root = mapper.readTree(rawChunk);

            if (root.has("error")){
                throw new RuntimeException(root.path("error").path("message").asText());
            }

            // 3. 提取内容
            JsonNode choice = root.path("choices").get(0);
            if (choice == null){
                return AIChunkResponse.empty();
            }

            if (choice.has("message")){
                String msg = choice.path("message").path("content").asText("");
                return AIChunkResponse.normalOutput(msg);
            }
            String content = choice.path("delta").path("content").asText("");
//            String finish_reason = root.path("choices").get(0).get("finish_reason").asText("");
//            if (StrUtil.isNotEmpty(finish_reason) && !"stop".equals(finish_reason)){
//                throw new RuntimeException(finish_reason);
//            }

            // 3. 判断是否Think DeepSeek R1特有
            if (root.path("usage").has("completion_tokens_details")) {
                if (content.equals("</think>")){
                    return AIChunkResponse.empty();
                }
                if (content.equals("<think>")){
                    return AIChunkResponse.startThink();
                }
                int totalToken = root.path("usage").path("completion_tokens").asInt(0);
                // 4. 判断是否正在思考
                int thinkToken = root.path("usage").path("completion_tokens_details").path("reasoning_tokens").asInt(0);
                if (totalToken == thinkToken) {
                    return AIChunkResponse.thinking(content);
                }
            }
            return AIChunkResponse.normalOutput(content);
        } catch (Exception e) {
            throw new RuntimeException("AI调用失败: " + e.getMessage());
        }
    }
}
