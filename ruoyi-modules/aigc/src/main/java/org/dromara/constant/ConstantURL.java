package org.dromara.constant;

/**
 * @author zrl
 * @date 2024/5/31
 */
public class ConstantURL {
    /**
     * 获取code的API地址
     */
    public static final String GET_DY_CODE = "https://open.douyin.com/platform/oauth/connect/";

    /**
    *获取访问令牌的API地址
    */
    public static final String GET_DY_ACCESS_TOKEN = "https://open.douyin.com/oauth/access_token/";

    /**
     * 获取用户的基础信息
     */
    public static final String GET_DY_USERINFO = "https://open.douyin.com/oauth/userinfo/";

    /**
     *  刷新刷新令牌的API地址
     */
    public static final String RENEW_DY_REFRESH_TOKEN = "https://open.douyin.com/oauth/renew_refresh_token/";

    /**
     * 刷新访问令牌的API地址
     */
    public static final String REFRESH_DY_ACCESS_TOKEN = "https://open.douyin.com/oauth/refresh_token/";

    /**
     * 启动影刀应用
     */
    public static final String START_YD = "https://api.winrobot360.com/oapi/dispatch/v2/job/start";

    /**
     * 获取影刀key
     */
    public static final String GET_YD_KEY = "https://api.yingdao.com/oapi/token/v2/token/create";

    /**
     * 获取抖音clientToken
     */
    public static final String GET_DY_CLIENT_TOKEN = "https://open.douyin.com/oauth/client_token/";

    /**
     * 获取抖音openTicket
     */
    public static final String GET_DY_OPEN_TICKET = "https://open.douyin.com/open/getticket/";


    public static final String DINGTALK_GET_USER_DETAIL_URL = "https://oapi.dingtalk.com/topapi/v2/user/get";
    public static final String DINGTALK_SEND_NOTIFY_MESSAGE_URL = "https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2";

    public static final String SSO_LOGIN_URL = "http://10.10.4.43/auth/ssoLogin?token=%s&salt=puqi&loginId=%s";
    public static final String DINGTALK_URL_PREFIX = "dingtalk://dingtalkclient/page/link?url=%s&pc_slide=false";
    public static final String ENCODE_TEMPLATE = "%s|%s|%s";

}
