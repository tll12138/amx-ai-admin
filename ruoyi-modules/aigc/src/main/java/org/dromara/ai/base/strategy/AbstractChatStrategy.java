package org.dromara.ai.base.strategy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.domain.AiModel;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractChatStrategy implements ChatStrategy {

    protected WebClient client = WebClient.builder().build();

    /**
     * 构建请求URI
     *
     * @param model 模型配置
     * @return 请求URI
     */
    protected String buildUri(AiModel model) {
        return model.getEndpoint() == null ? model.getBaseUrl() : model.getBaseUrl() + model.getEndpoint();
    }


    /**
     * 构建请求头
     *
     * @param headers 请求头
     * @param config  模型配置
     */
    protected void buildRequestHeaders(HttpHeaders headers, AiModel config) {
        headers.set("Authorization", "Bearer " + config.getApiKey());
    }


    /**
     * 构建请求体
     *
     * @param prompt 请求参数
     * @return 请求体
     */
    protected Map<String, Object> buildRequestBody(AiModel model, Map<String, Object> prompt) {
        String messages = (String) prompt.get("messages");

        if (prompt.containsKey("system") && StrUtil.isNotEmpty(prompt.get("system").toString())) {
            String system = (String) prompt.get("system");
            // 默认使用OPENAI MODEL
            prompt.remove("system");
            // 覆盖messages 内容
            prompt.put("messages", List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user", "content", messages)
            ));
        }else{
            prompt.put("messages", List.of(
                Map.of("role", "user", "content", messages)
            ));
        }
        log.info("调用AI模型参数:{}", JSONUtil.toJsonStr(prompt));
        return prompt;
    }


    /**
     * 流式生成
     *
     * @param model  模型配置
     * @param prompt 请求参数
     * @return 生成的文本流
     */
    @Override
    public Flux<String> streamGenerate(AiModel model, Map<String, Object> prompt) {
        String uri = buildUri(model);
        log.info("uri:{}", uri);

        return client.post()
            .uri(uri)
            .headers(headers -> buildRequestHeaders(headers, model))
            .bodyValue(buildRequestBody(model, prompt))
            .retrieve()
            .bodyToFlux(String.class);
    }
    @Override
    public String chatGenerate(AiModel model, Map<String, Object> prompt) {
        String uri = buildUri(model);
        log.info("uri:{}", uri);
        return HttpUtil.createPost(uri)
            .header("Authorization", "Bearer " + model.getApiKey())
            .body(JSONUtil.toJsonStr(buildRequestBody(model, prompt)))
            .execute()
            .body();
    }


}
