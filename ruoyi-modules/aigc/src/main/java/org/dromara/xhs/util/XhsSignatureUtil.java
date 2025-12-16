package org.dromara.xhs.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 小红书签名工具类
 */
public class XhsSignatureUtil {

    /**
     * 加签demo 生成sigature 工具
     * @param appKey 唯一标识
     * @param nonce 随机字符串，随机生成-需要和接口请求中保持一致
     * @param timeStamp 当前毫秒级时间戳-例如 2023-08-15 20:31:31 对应时间戳 1692102691696-需要和接口请求中保持一致
     * @param appSecret 1、获取access_token第一次加签，使用密钥appSecret 2、分享秘钥生成第二次加签，使用access_token
     * @return signature 签名
     * @throws Exception
     */
    public static String buildSignature( String nonce, Long timeStamp,String appKey, String appSecret) throws Exception{
        Map<String, String> params = new HashMap<>();
        params.put("appKey", appKey);
        params.put("nonce", nonce);
        params.put("timeStamp", timeStamp.toString());
        return generateSignature(appSecret, params);
    }
    /**
     * 构建
     * @param secretKey 密钥
     * @param params 加签参数
     * @return 签名
     */
    public static String generateSignature(String secretKey, Map<String, String> params) {
        // Step 1: Sort parameters by key
        Map<String, String> sortedParams = new TreeMap<>(params);
        // Step 2: Concatenate sorted parameters
        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (paramsString.length() > 0) {
                paramsString.append("&");
            }
            paramsString.append(entry.getKey()).append("=").append(entry.getValue());
        }
        // Step 3: Add secret key to the parameter string
        paramsString.append(secretKey);
        // Step 4: Calculate signature using SHA-256
        String signature = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(paramsString.toString().getBytes(StandardCharsets.UTF_8));
            // Convert the byte array to hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            signature = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        return signature;
    }

    /**
     * 生成随机字符串
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateNonce(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
