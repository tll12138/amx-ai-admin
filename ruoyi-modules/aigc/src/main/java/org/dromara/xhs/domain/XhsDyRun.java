package org.dromara.xhs.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class XhsDyRun {

    @JsonProperty("cookie_id")
    private Long cookieId;

    private String account;

    @JsonProperty("data_list")
    private List<XhsDyRunItem> dataList;
}
