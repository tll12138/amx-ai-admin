package org.dromara.rp.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.rp.domain.RpAccount;

/**
 * 账号信息业务对象 rp_account
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpAccount.class, reverseConvertGenerate = false)
public class RpAccountBo extends BaseDo {

    /**
     * 账号ID
     */
    @NotNull(message = "账号ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 账号名称
     */
    @NotBlank(message = "账号名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String accountName;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 账号平台（冗余字段，用于快速筛选）
     */
    @NotBlank(message = "账号平台（冗余字段，用于快速筛选）不能为空", groups = {AddGroup.class, EditGroup.class})
    private String platform;

    /**
     * 所属分组ID
     */
    @NotNull(message = "所属分组ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long groupId;

    /**
     * 账号状态（1启用 0禁用）
     */
    private Long status;

    /**
     * 额外信息
     */
    private String extraInfo;


}
