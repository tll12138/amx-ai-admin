package org.dromara.ai.domain.option;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author zrl
 * @date 2025/4/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModelOptions extends BaseOptions{


    /**
     * 模型名称
     */
    private String provider;

    /**
     * 随机性
     */
    private Double temperature;

    /**
     * 多样性
     */
    private Double topP;

}
