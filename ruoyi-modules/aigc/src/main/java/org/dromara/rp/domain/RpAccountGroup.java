package org.dromara.rp.domain;

import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 账号分组对象 rp_account_group
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_account_group")
public class RpAccountGroup extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 分组ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;
        /**
         * 分组名称
         */
        private String groupName;
        /**
         * 分组所属平台（如: WeChat, Douyin, Weibo 等）
         */
        private String platform;
        /**
         * 额外信息
         */
        private String extraInfo;
        /**
         * 关联rpa账号
         */
        private Integer rpa_no;

}
