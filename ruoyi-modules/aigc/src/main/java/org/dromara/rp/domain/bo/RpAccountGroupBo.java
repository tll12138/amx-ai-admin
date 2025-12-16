package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.RpAccountGroup;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 账号分组业务对象 rp_account_group
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpAccountGroup.class, reverseConvertGenerate = false)
public class RpAccountGroupBo extends BaseDo {

        /**
         * 分组ID
         */
        @NotNull(message = "分组ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 分组名称
         */
        @NotBlank(message = "分组名称不能为空", groups = { AddGroup.class, EditGroup.class })
        private String groupName;

        /**
         * 分组所属平台（如: WeChat, Douyin, Weibo 等）
         */
        @NotBlank(message = "分组所属平台（如: WeChat, Douyin, Weibo 等）不能为空", groups = { AddGroup.class, EditGroup.class })
        private String platform;

        /**
         * 额外信息
         */
        private String extraInfo;


}
