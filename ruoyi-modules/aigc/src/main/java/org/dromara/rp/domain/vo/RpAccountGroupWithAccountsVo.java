package org.dromara.rp.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class RpAccountGroupWithAccountsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String groupName;

    private List<RpAccountBriefVo> accounts;
}