package org.dromara.rp.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RpAccountBriefVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String accountName;

    private String deviceCode;
}