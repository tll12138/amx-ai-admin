package org.dromara.ai.base.factory;

import org.dromara.ai.base.enums.ModelProvider;
import org.dromara.ai.base.parser.ResponseParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ResponseParserFactory {

    private final Map<ModelProvider, ResponseParser> parsers = new ConcurrentHashMap<>();

    @Autowired
    public ResponseParserFactory(List<ResponseParser> responseParsers) {
        for (ResponseParser parser : responseParsers) {
            for (ModelProvider provider : parser.getSupportedProviders()) {
                if (!parsers.containsKey(provider)) {
                    parsers.put(provider, parser);
                }
            }
        }
    }

    public ResponseParser getParser(ModelProvider provider) {
        return parsers.getOrDefault(provider,
            parsers.get(ModelProvider.OPENAI)
        ); // 默认回退到 OpenAI
    }
}
