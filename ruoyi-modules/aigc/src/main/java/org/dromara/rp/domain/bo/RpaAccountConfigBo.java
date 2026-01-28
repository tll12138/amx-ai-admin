package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.RpaAccountConfig;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * RPA账号配置业务对象 rpa_account_config
 *
 * @author LL
 * @date 2026-01-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpaAccountConfig.class, reverseConvertGenerate = false)
public class RpaAccountConfigBo extends BaseDo {

        /**
         * 主键ID
         */
        @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 账号名称
         */
        @NotBlank(message = "账号名称不能为空", groups = { AddGroup.class, EditGroup.class })
        private String robotClientName;

        /**
         * 账号唯一标识
         */
        @NotBlank(message = "账号唯一标识不能为空", groups = { AddGroup.class, EditGroup.class })
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
