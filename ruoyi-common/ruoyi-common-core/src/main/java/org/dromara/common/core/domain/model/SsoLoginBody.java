package org.dromara.common.core.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

/**
 * 密码登录对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SsoLoginBody extends LoginBody {

    /**
     * 用户名
     */
    @NotBlank(message = "{user.token.not.blank}")
    private String token;

}
