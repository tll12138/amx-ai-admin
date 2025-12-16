package org.dromara.ai.utils.dd;

import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalktodo_1_0.Client;
import com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskHeaders;
import com.aliyun.dingtalktodo_1_0.models.UpdateTodoTaskRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiV2UserGetRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.taobao.api.ApiException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.dromara.ai.utils.dd.vo.StarMonitorDingNotifyVO;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.ai.utils.dd.config.DingTalkProperties;
import org.dromara.ai.utils.dd.redis.DingTalkAccessTokenRedisDAO;
import org.dromara.ai.utils.dd.redis.DingTalkUserUnionRedisDAO;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.dromara.constant.ConstantURL.DINGTALK_GET_USER_DETAIL_URL;
import static org.dromara.constant.ConstantURL.DINGTALK_SEND_NOTIFY_MESSAGE_URL;

/**
 * @author zrl
 * @date 2024/10/31
 */
@Service
@Slf4j
public class DingTalkUtils {

    @Resource
    private DingTalkProperties dingTalkProperties;

    @Resource
    private DingTalkAccessTokenRedisDAO dingTalkRedisDAO;

    @Resource
    private DingTalkUserUnionRedisDAO userUnionRedisDAO;


    // ==================================== 钉钉Token 相关API ===========================

    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkoauth2_1_0.Client createTokenClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    /**
     * 使用 Token 初始化账号Client
     *
     * @return Client
     * @throws Exception
     */
    public static Client createTaskClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new Client(config);
    }

    // ==================================== 钉钉用户 相关API ===========================

    /**
     * 封装请求获取钉钉AccessToken
     *
     * @return token
     * @throws Exception 异常信息
     */
    public String getAccessToken() throws Exception {
        String token = dingTalkRedisDAO.get();
        if (token != null) {
            return token;
        }
        com.aliyun.dingtalkoauth2_1_0.Client tokenClient = createTokenClient();
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest()
                .setAppKey(dingTalkProperties.getClientId())
                .setAppSecret(dingTalkProperties.getClientSecret());
        try {
            GetAccessTokenResponse accessToken = tokenClient.getAccessToken(getAccessTokenRequest);
            if (!accessToken.statusCode.equals(200) || com.aliyun.teautil.Common.empty(accessToken.body.accessToken)) {
                log.error("[getAccessToken][获取钉钉 AccessToken 失败]");
            }
            //加入REDIS 缓存中
            log.info("[getAccessToken][获取钉钉 AccessToken 成功]-{}", accessToken.body.accessToken);
            dingTalkRedisDAO.set(accessToken.body.accessToken, accessToken.body.expireIn);
            return accessToken.body.accessToken;
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("[getAccessToken][获取钉钉 AccessToken 失败],错误信息为：{}", err.message);
            }
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("[getAccessToken][获取钉钉 AccessToken 失败],错误信息为：{}", err.message);
            }
        }
        throw new ServiceException("获取钉钉 AccessToken 失败");
    }


    // ==================================== 钉钉消息通知 相关API ===========================

    /**
     * 通过userId 获取用户的 unionId
     * @param  userId 用户的userId
     * @return 用户的 unionId
     * @throws Exception 错误信息
     */
    public String getUnionId(String userId) throws Exception {
        String unionId = userUnionRedisDAO.get(userId);
        if (unionId != null) {
            return unionId;
        }
        try {
            DingTalkClient client = new DefaultDingTalkClient(DINGTALK_GET_USER_DETAIL_URL);
            OapiV2UserGetRequest req = new OapiV2UserGetRequest();
            req.setUserid(userId);
            req.setLanguage("zh_CN");
            OapiV2UserGetResponse rsp = client.execute(req, getAccessToken());
            if (!rsp.isSuccess() || rsp.getResult() == null
                    || com.aliyun.teautil.Common.empty(rsp.getResult().getUnionid())) {
                log.error("[getUserInfo][获取用户信息失败],用户ID为{}", userId);
                throw new ServiceException("获取用户信息失败");
            }
            userUnionRedisDAO.set(userId, rsp.getResult().getUnionid());
            return rsp.getResult().getUnionid();
        } catch (ApiException err) {
            if (!com.aliyun.teautil.Common.empty(err.getErrCode()) && !com.aliyun.teautil.Common.empty(err.getErrMsg())) {
                log.error("[getUserInfo][获取用户信息失败],错误信息为：{}，用户ID为{}", err.getErrMsg(), userId);
            }
        }
        throw new ServiceException("获取用户信息失败");
    }


    // ==================================== 钉钉代办 相关API ===========================

    /**
     * 发送钉钉通知消息（单个按钮）
     *
     * @param userId      用户 userID
     * @param title       标题 发送内容的标题
     * @param description 描述 发送内容的描述，会跟title 进行拼接，结果为markdown模式
     * @param url         处理链接地址
     * @param actionTitle 通知的来源 显示在钉钉通知中的来源
     * @param buttonText  按钮文案
     * @return 发送结果
     * @throws Exception 异常信息
     */
    public void sendNotifyMessage(String userId, String title,
                                            String description, String url, String actionTitle, String buttonText) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient(DINGTALK_SEND_NOTIFY_MESSAGE_URL);
        OapiMessageCorpconversationAsyncsendV2Request request =
                new OapiMessageCorpconversationAsyncsendV2Request();
        request.setAgentId(Long.valueOf(dingTalkProperties.getAgentId()));
        request.setUseridList(userId);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg =
                new OapiMessageCorpconversationAsyncsendV2Request.Msg();

        msg.setMsgtype("action_card");
        msg.setActionCard(new OapiMessageCorpconversationAsyncsendV2Request.ActionCard());
        msg.getActionCard().setTitle(actionTitle);
        String descriptionMarkdown = "### **" + title + "** \n  "  + description;
        msg.getActionCard().setMarkdown(descriptionMarkdown);
        msg.getActionCard().setSingleTitle(buttonText);
        msg.getActionCard().setSingleUrl(url);
        request.setMsg(msg);
        try {
            OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, getAccessToken());
            log.info("[sendNotifyMessage][发送钉钉通知成功，消息ID为:{}]", rsp.getTaskId());
        } catch (ApiException err) {
           log.error("[sendNotifyMessage][发送钉钉通知失败，错误信息为:{}]", err.getMessage());
        }
    }

    /**
     * 更新钉钉代办
     * @param unionId 操作者unionId
     * @param taskId 代办ID
     * @throws Exception 异常信息
     */
    public void updateTodoTask(String unionId, String taskId) throws Exception {
        Client client = createTaskClient();
        UpdateTodoTaskHeaders updateTodoTaskHeaders = new UpdateTodoTaskHeaders();
        updateTodoTaskHeaders.xAcsDingtalkAccessToken = getAccessToken();
        UpdateTodoTaskRequest updateTodoTaskRequest = new UpdateTodoTaskRequest()
                .setOperatorId(unionId)
                .setDone(true);
        try {
            client.updateTodoTaskWithOptions(unionId, taskId, updateTodoTaskRequest, updateTodoTaskHeaders, new RuntimeOptions());
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("[updateTodoTask][更新钉钉代办失败],错误信息为：{}", err.message);
            }

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
                log.error("[updateTodoTask][更新钉钉代办失败],错误信息为：{}", err.message);
            }
        }
    }


    public Boolean sendWebHookMessage(String secret, String accessToken, StarMonitorDingNotifyVO notifyVO) {
        try {
            // 加密信息
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8);


            DingTalkClient client =
                new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?sign=" + sign + "&timestamp=" + timestamp);
            OapiRobotSendRequest req = new OapiRobotSendRequest();
            // 定义at对象
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setIsAtAll(notifyVO.getAtAll() != null && notifyVO.getAtAll());

            // 定义文本内容
            OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
            markdown.setTitle(notifyVO.getTitle());
            markdown.setText(notifyVO.getContent());

            //设置消息类型
            req.setMsgtype("markdown");
            req.setMarkdown(markdown);
            req.setAt(at);
            OapiRobotSendResponse rsp = client.execute(req, accessToken);
            System.out.println(rsp.getBody());
            return "ok".equals(rsp.getErrmsg());
        } catch (ApiException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}
