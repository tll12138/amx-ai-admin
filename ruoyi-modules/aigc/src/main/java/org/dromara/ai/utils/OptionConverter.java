package org.dromara.ai.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.domain.option.BaseGroupOption;
import org.dromara.ai.domain.option.BaseOptions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class OptionConverter {

    /**
     * 将任意对象列表转换为 BaseOptions 列表
     *
     * @param dataList    数据列表
     * @param labelColumn label字段 getter
     * @param valueColumn value字段 getter
     * @return List<BaseOptions>
     */
    public static <T, L, V, O> List<BaseOptions> convertToOptions(
        List<T> dataList,
        Function<T, L> labelColumn,
        Function<T, V> valueColumn,
        Function<T, O> otherColumn) {

        return dataList.stream()
            .map(item -> buildOption(item, labelColumn, valueColumn, otherColumn))
            .collect(Collectors.toList());
    }

    /**
     * 泛型方式，将对象列表转换为 BaseGroupOption 结构
     *
     * @param dataList      数据列表
     * @param groupByColumn 分组字段 getter
     * @param labelColumn   label字段 getter
     * @param valueColumn   value字段 getter
     * @return List<BaseGroupOption>
     */
    public static <T, G, L, V, O> List<BaseGroupOption> convertToGroupOptions(
        List<T> dataList,
        Function<T, G> groupByColumn,
        Function<T, L> labelColumn,
        Function<T, V> valueColumn
    ) {
        return convertToGroupOptions(dataList, groupByColumn, labelColumn, valueColumn, null);
    }


    /**
     * 泛型方式，将对象列表转换为 BaseGroupOption 结构
     *
     * @param dataList      数据列表
     * @param groupByColumn 分组字段 getter
     * @param labelColumn   label字段 getter
     * @param valueColumn   value字段 getter
     * @return List<BaseGroupOption>
     */
    public static <T, G, L, V, O> List<BaseGroupOption> convertToGroupOptions(
        List<T> dataList,
        Function<T, G> groupByColumn,
        Function<T, L> labelColumn,
        Function<T, V> valueColumn,
        Function<T, O> otherColumn
    ) {

        Map<String, List<BaseOptions>> groupedMap = dataList.stream()
            .collect(Collectors.groupingBy(
                item -> Optional.ofNullable(groupByColumn.apply(item)).map(Object::toString).orElse("未知"),
                java.util.LinkedHashMap::new, // 保持分组顺序
                Collectors.mapping(
                    item -> buildOption(item, labelColumn, valueColumn, otherColumn),
                    Collectors.toList()
                )
            ));

        return groupedMap.entrySet().stream()
            .map(entry -> {
                BaseGroupOption group = new BaseGroupOption();
                group.setLabel(entry.getKey());
                group.setOptions(entry.getValue());
                return group;
            })
            .collect(Collectors.toList());
    }

    /**
     * 构建单个 BaseOptions 实例
     */
    private static <T, L, V, O> BaseOptions buildOption(
        T item,
        Function<T, L> labelColumn,
        Function<T, V> valueColumn
    ) {
        return buildOption(item, labelColumn, valueColumn, null);
    }

    /**
     * 构建单个 BaseOptions 实例
     */
    private static <T, L, V, O> BaseOptions buildOption(
        T item,
        Function<T, L> labelColumn,
        Function<T, V> valueColumn,
        Function<T, O> otherColumn
    ) {

        BaseOptions option = new BaseOptions();
        option.setLabel(Optional.ofNullable(labelColumn.apply(item)).map(Object::toString).orElse(""));
        option.setValue(Optional.ofNullable(valueColumn.apply(item)).map(Object::toString).orElse(""));
        if (otherColumn != null){
            option.setOther(Optional.ofNullable(otherColumn.apply(item)).map(Object::toString).orElse(""));
        }
        return option;
    }
}
