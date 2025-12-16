package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author zrl
 * @date 2025/4/2
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xhs_cookies")
public class XhsCookie implements Serializable {


    private String phone;

    private String cookie;
}
