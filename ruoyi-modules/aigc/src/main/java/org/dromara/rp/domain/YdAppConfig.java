package org.dromara.rp.domain;

import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 影刀应用配置对象 yd_app_config
 *
 * @author LL
 * @date 2026-03-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("yd_app_config")
public class YdAppConfig extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;
        /**
         * 平台
         */
        private String platform;
        /**
         * 设备类型
         */
        private String deviceType;
        /**
         * 应用id
         */
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
