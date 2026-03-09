package org.dromara.rp.domain;

import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 比特账号信息对象 rp_bit_account
 *
 * @author LL
 * @date 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_bit_account")
public class RpBitAccount extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 比特账号ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;
        /**
         * 账号名称
         */
        private String accountName;
        /**
         * 账号编码
         */
        private String accountCode;
        /**
         * 账号状态
         */
        private Long status;

}
