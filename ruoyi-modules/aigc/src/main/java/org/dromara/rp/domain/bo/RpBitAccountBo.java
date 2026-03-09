package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.RpBitAccount;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 比特账号信息业务对象 rp_bit_account
 *
 * @author LL
 * @date 2026-03-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpBitAccount.class, reverseConvertGenerate = false)
public class RpBitAccountBo extends BaseDo {

        /**
         * 比特账号ID
         */
        @NotNull(message = "比特账号ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 账号名称
         */
        @NotBlank(message = "账号名称不能为空", groups = { AddGroup.class, EditGroup.class })
        private String accountName;

        /**
         * 账号编码
         */
        @NotBlank(message = "账号编码不能为空", groups = { AddGroup.class, EditGroup.class })
        private String accountCode;

        /**
         * 账号状态
         */
        private Long status;


}
