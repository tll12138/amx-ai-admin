package org.dromara.ai.domain.option;

import lombok.Data;

import java.util.List;

/**
 * @author zrl
 * @date 2025/4/25
 */
@Data
public class BaseGroupOption {

    private String label;

    private List<BaseOptions> options;
}
