package org.dromara.ai.domain.map;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author zrl
 * @date 2025/4/25
 */
@Component
@Named("ConvertStrAndList")
public class ConvertStrAndList{

    @Named("Str2List")
    public List<String> Str2List(String str) {
        return str != null ? Arrays.asList(str.split(",")) : null;
    }

    @Named("List2Str")
    public String List2Str(List<String> list) {
        return list != null ? String.join(",", list) : null;
    }
}
