package org.dromara.ai.base.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.exception.ModelNotFoundException;
import org.dromara.ai.base.factory.AIStrategyFactory;
import org.dromara.ai.base.factory.ModelFactory;
import org.dromara.ai.base.factory.ResponseParserFactory;
import org.dromara.ai.base.parser.ResponseParser;
import org.dromara.ai.base.strategy.ChatStrategy;
import org.dromara.ai.domain.AiModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIBaseService {

    private final ModelFactory modelFactory;
    private final AIStrategyFactory strategyFactory;
    private final ResponseParserFactory parserFactory;

    public Flux<String> streamGenerate(AiModel model, Map<String, Object> prompt) {

        // 根据 modelId 查询对应的模型配置信息
        Map<String, Object> modelConfig = modelFactory.getModelConfig(model);
        prompt.put("stream", true);
        prompt.putAll(modelConfig);

        ModelProvider provider = ModelProvider.getByProvider(model.getProvider());

        // 根据 provider 获取合适的策略
        ChatStrategy strategy = strategyFactory.getStrategy(provider);

        // 根据 provider 返回对应的结果解析器
        ResponseParser parser = parserFactory.getParser(provider);

        // 调用策略的流式生成方法，并解析返回结果
        return strategy.streamGenerate(model, prompt)
            .map(parser::parseResponse);// 解析返回内容
    }

    public String chatGenerate(AiModel model, Map<String, Object> prompt) {

        // 根据 modelId 查询对应的模型配置信息
        Map<String, Object> modelConfig = modelFactory.getModelConfig(model);
        prompt.putAll(modelConfig);

        ModelProvider provider = ModelProvider.getByProvider(model.getProvider());

        // 根据 provider 获取合适的策略
        ChatStrategy strategy = strategyFactory.getStrategy(provider);

        // 根据 provider 返回对应的结果解析器
        ResponseParser parser = parserFactory.getParser(provider);

        // 调用策略的对话生成方法，并解析返回结果
        String content = strategy.chatGenerate(model, prompt);
        return parser.parseResponse(content);
    }

    public String testChat(AiModel model, String userMessage) {
        if (model == null) {
            throw new ModelNotFoundException("模型信息错误");  // 模型未找到
        }
        Map<String, Object> prompt = new HashMap<>();
        prompt.put("messages", userMessage);

        // 调用策略的生成方法
        return this.chatGenerate(model, prompt);
    }
}
