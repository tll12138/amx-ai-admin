package org.dromara.ai.base.bo;

import lombok.Data;

/**
 * @author zrl
 * @date 2025/5/7
 */

@Data
public class AiBaseBo {

    private String model;

    private String style;

    private String systemMessage;

    private String userMessage;

    /**
     * 模型随机性
     */
    private Double temperature;

    /**
     * 模型多样性
     */
    private Double topP;
}
