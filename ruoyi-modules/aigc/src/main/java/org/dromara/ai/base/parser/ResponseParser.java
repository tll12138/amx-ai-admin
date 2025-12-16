package org.dromara.ai.base.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dromara.ai.base.enums.ModelProvider;

import java.util.Set;

public interface ResponseParser {

    ObjectMapper mapper = new ObjectMapper();
    Set<ModelProvider> getSupportedProviders(); // 获取平台名称
    String parseResponse(String response);
}
