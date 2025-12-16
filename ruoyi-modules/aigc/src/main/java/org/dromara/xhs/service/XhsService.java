package org.dromara.xhs.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.xhs.config.XhsConfig;
import org.dromara.xhs.domain.XhsTokenResponseData;
import org.dromara.xhs.domain.vo.signature.XhsSignatureRequest;
import org.dromara.xhs.domain.vo.signature.XhsSignatureResponse;
import org.dromara.xhs.domain.vo.signature.XhsTokenRequest;
import org.dromara.xhs.domain.vo.signature.XhsTokenResponse;
import org.dromara.xhs.util.XhsSignatureUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 小红书服务类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class XhsService {

    private final XhsConfig xhsConfig;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String ACCESS_TOKEN_KEY = "xhs:access_token";

    /**
     * 获取access_token
     */
    public XhsTokenResponseData getAccessToken(XhsTokenRequest request) {
        try {
            // 检查缓存中是否有有效的token
            String cachedToken = redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
            if (StrUtil.isNotEmpty(cachedToken)) {
                log.info("从缓存中获取access_token: " + cachedToken);
                return new XhsTokenResponseData(cachedToken, xhsConfig.getTokenExpireTime());
            }

            // 生成签名
            String nonce = request.getNonce();
            Long timestamp = request.getTimestamp();
            String signature = XhsSignatureUtil.buildSignature(
                nonce, timestamp, xhsConfig.getAppKey(), xhsConfig.getAppSecret()
            );

            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("app_key", xhsConfig.getAppKey());
            params.put("nonce", nonce);
            params.put("timestamp", request.getTimestamp());
            params.put("signature", signature);
            if (request.getExpiresIn() != null) {
                params.put("expires_in", request.getExpiresIn());
            }

            // 发送请求到小红书API
            HttpRequest post = HttpUtil.createPost(xhsConfig.getTokenUrl());
            post.header("Content-Type", "application/json");
            post.body(JSONUtil.toJsonStr(params));
            HttpResponse execute = post.execute();
            log.info("请求结果:" + execute);
            String respData = execute.body();

            XhsTokenResponse resp = JSONUtil.toBean(respData, XhsTokenResponse.class);
            XhsTokenResponseData data = resp.getData();
            String accessToken = data.getAccessToken();
            Long expiresIn = data.getExpiresIn();

            // 缓存token
            redisTemplate.opsForValue().set(
                ACCESS_TOKEN_KEY, accessToken,
                7200, TimeUnit.SECONDS
            );

            return new XhsTokenResponseData(accessToken, expiresIn);
        } catch (Exception e) {
            throw new RuntimeException("获取access_token异常: " + e.getMessage(), e);
        }
    }

    /**
     * 获取分享签名
     */
    public XhsSignatureResponse getShareSignature(XhsSignatureRequest request) {
        try {

            XhsTokenRequest xhsTokenRequest = new XhsTokenRequest();
            xhsTokenRequest.setNonce(request.getNonce());
            xhsTokenRequest.setTimestamp(request.getTimestamp());
            // 获取access_token
            XhsTokenResponseData xhsTokenResponse = getAccessToken(xhsTokenRequest);
            log.info("xhsTokenResponse: " + xhsTokenResponse.toString());

            String signature = XhsSignatureUtil.buildSignature(
                request.getNonce(),
                request.getTimestamp(),
                request.getAppKey(),
                xhsTokenResponse.getAccessToken()
            );

            return new XhsSignatureResponse(
                request.getAppKey(),
                request.getNonce(),
                request.getTimestamp(),
                signature
            );
        } catch (Exception e) {
            throw new RuntimeException("获取分享签名异常: " + e.getMessage(), e);
        }
    }

    public void refreshToken() {
        log.info("删除Redis缓存Token");
        redisTemplate.delete(ACCESS_TOKEN_KEY);
    }
}
