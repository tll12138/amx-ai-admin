package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.YdAppConfig;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 影刀应用配置业务对象 yd_app_config
 *
 * @author LL
 * @date 2026-03-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = YdAppConfig.class, reverseConvertGenerate = false)
public class YdAppConfigBo extends BaseDo {

        /**
         * 主键ID
         */
        @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 平台
         */
        @NotBlank(message = "平台不能为空", groups = { AddGroup.class, EditGroup.class })
        private String platform;

        /**
         * 设备类型
         */
        @NotBlank(message = "设备类型不能为空", groups = { AddGroup.class, EditGroup.class })
        private String deviceType;

        /**
         * 应用id
         */
        @NotBlank(message = "应用id不能为空", groups = { AddGroup.class, EditGroup.class })
        private String applicationId;

        /**
         * 状态 (0正常 1异常)
         */
        private String status;

        /**
         * 备注
         */
        private String remark;


}
