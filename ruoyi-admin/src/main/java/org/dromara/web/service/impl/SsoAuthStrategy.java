package org.dromara.web.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.domain.model.LoginUser;
import org.dromara.common.core.domain.model.PasswordLoginBody;
import org.dromara.common.core.domain.model.SsoLoginBody;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.base.BaseException;
import org.dromara.common.core.exception.user.CaptchaException;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.exception.user.UserException;
import org.dromara.common.core.utils.EncodeAndDecodeUtils;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.web.config.properties.CaptchaProperties;
import org.dromara.system.domain.SysUser;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.mapper.SysUserMapper;
import org.dromara.web.domain.vo.LoginVo;
import org.dromara.web.service.IAuthStrategy;
import org.dromara.web.service.SysLoginService;
import org.springframework.stereotype.Service;

import static org.dromara.common.core.constant.SystemConstants.SSO_SECRET;
import static org.dromara.common.core.constant.TenantConstants.DEFAULT_TENANT_ID;

/**
 * 密码认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("sso" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class SsoAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    private final SysLoginService loginService;
    private final SysUserMapper userMapper;

    @Override
    public LoginVo login(String token, SysClientVo client) {
        SsoLoginBody loginBody = JsonUtils.parseObject(token, SsoLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String decode = "";
        try {
            decode = EncodeAndDecodeUtils.decode(loginBody.getToken());
            if (StrUtil.isEmpty(decode)){
                throw new RuntimeException();
            }
        }catch (Exception e){
            throw new UserException("SSO 登录凭证错误： 无效的账号");
        }
        String[] decodeData = decode.split("\\|");
        String username = decodeData[0];
        String secret = decodeData[2];
        if (!secret.equals(SSO_SECRET)){
            throw new BaseException("SSO 登录凭证错误：错误的密钥");
        }

        String timeStamp = decodeData[1];
        //获取当前的时间戳，如果当前时间戳-登录时间戳>10秒则认为此次登录已过期
        long currentTime = System.currentTimeMillis();
        if (currentTime - Long.parseLong(timeStamp) > 1000 * 600) {
            throw new UserException("SSO 登录凭证错误： Token已过期");
        }

        LoginUser loginUser = TenantHelper.dynamic(DEFAULT_TENANT_ID, () -> {
            // 判断是否存在该用户
            SysUserVo user = loadUserByUserName(username);
            // 存在则登录； 此处可根据登录用户的数据不同 自行创建 loginUser
            return loginService.buildLoginUser(user);
        });
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        return loginVo;
    }



    private SysUserVo loadUserByUserName(String userName) {
        SysUserVo user = userMapper.selectVoOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserName, userName));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", userName);
            throw new UserException("user.not.exists", userName);
        } else if (SystemConstants.DISABLE.equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", userName);
            throw new UserException("user.blocked");
        }
        return user;
    }

}
