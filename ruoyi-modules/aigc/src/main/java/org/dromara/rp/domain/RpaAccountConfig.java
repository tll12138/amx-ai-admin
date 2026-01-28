package org.dromara.rp.domain;

import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * RPA账号配置对象 rpa_account_config
 *
 * @author LL
 * @date 2026-01-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rpa_account_config")
public class RpaAccountConfig extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;
        /**
         * 账号名称
         */
        private String robotClientName;
        /**
         * 账号唯一标识
         */
        private String robotClientUuid;
        /**
         * 状态 (0正常 1异常)
         */
        private String status;
        /**
         * 备注
         */
        private String remark;

}
