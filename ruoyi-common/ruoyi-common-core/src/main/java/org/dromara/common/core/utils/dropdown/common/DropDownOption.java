package org.dromara.common.core.utils.dropdown.common;

import lombok.Data;

/**
 * @author zrl
 * @date 2025/11/13
 */

@Data
public class DropDownOption<V> {

    private String label;

    private V value;

    private String extraData;
}
