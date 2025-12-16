package org.dromara.rp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * 账号信息对象 rp_account
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_account")
public class RpAccount extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 账号名称
     */
    private String accountName;
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 账号平台（冗余字段，用于快速筛选）
     */
    private String platform;
    /**
     * 所属分组ID
     */
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
