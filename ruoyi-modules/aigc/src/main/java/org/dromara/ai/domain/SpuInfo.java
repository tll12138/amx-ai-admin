package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zrl
 * @date 2025/4/28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("puqi_spu_info")
public class SpuInfo {

    private String spu;

    private String brand;

    private String keywords;
}
