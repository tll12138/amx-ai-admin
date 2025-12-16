package org.dromara.common.core.utils.dropdown;

import org.dromara.common.core.utils.dropdown.common.DropDownOption;
import org.dromara.common.core.utils.dropdown.common.DropDownOptionsGroup;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DropdownConverter {

    /**
     * 将对象列表转换为下拉框分组格式
     *
     * @param list 原始数据列表
     * @param groupField 分组字段名
     * @param optionLabelField 选项显示名字段
     * @param optionValueField 选项值字段
     * @param <T> 泛型类型
     * @return 转换后的下拉框数据
     */
    public static <T, V> List<DropDownOptionsGroup<V>> convertToDropdown(
            List<T> list,
            Function<T, String> groupField,
            Function<T, String> optionLabelField,
            Function<T, V> optionValueField
    ) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 按 groupField 分组
        Map<String, List<T>> grouped = list.stream()
                .collect(Collectors.groupingBy(groupField));

        // 构建最终结果
        List<DropDownOptionsGroup<V>> result = new ArrayList<>();

        for (Map.Entry<String, List<T>> entry : grouped.entrySet()) {
            DropDownOptionsGroup<V> dropDownOptionsGroup = new DropDownOptionsGroup<V>();
            String groupLabel = entry.getKey();
            dropDownOptionsGroup.setLabel(groupLabel);

            List<DropDownOption<V>> options = entry.getValue().stream()
                    .map(item -> {
                        DropDownOption<V> dropDownOption = new DropDownOption<V>();
                        dropDownOption.setLabel(optionLabelField.apply(item));
                        dropDownOption.setValue(optionValueField.apply(item));
                        dropDownOption.setExtraData(groupLabel);
                        return dropDownOption;
                    })
                    .collect(Collectors.toList());

            dropDownOptionsGroup.setOptions(options);
            result.add(dropDownOptionsGroup);
        }

        return result;
    }
}
