package org.dromara.ai.domain.option;

import lombok.Data;

/**
 * @author zrl
 * @date 2025/4/7
 */
@Data
public class BaseOptions {


    /**
     * 标签
     */
    private String label;

    /**
     * 值
     */
    private String value;

    /**
     * 其他信息
     */
    private String other;

}
