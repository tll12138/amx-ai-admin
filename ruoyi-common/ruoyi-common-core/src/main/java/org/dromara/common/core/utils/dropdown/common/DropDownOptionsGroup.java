package org.dromara.common.core.utils.dropdown.common;

import lombok.Data;

import java.util.List;

/**
 * @author zrl
 * @date 2025/11/13
 */
@Data
public class DropDownOptionsGroup<V> {

    private String label;

    private List<DropDownOption<V>> options;
}
