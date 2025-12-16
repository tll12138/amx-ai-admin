package org.dromara.ai.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.service.DictService;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.dromara.constant.ConstantURL.GET_YD_KEY;
import static org.dromara.constant.ConstantURL.START_YD;

/**
 * 影刀操作工具类
 *
 * @author zrl
 * @date 2024/5/30
 */
@Component
@Slf4j
public class YDUtils {

    @Resource
    private DictService dictDataService;


    public String getAccessTokenWithRedis() {
        //从Redis获取AccessKey
        Object ydKey = RedisUtils.getCacheObject("yd_key");
        if (ydKey != null) {
            return ydKey.toString();
        } else {
            return getAccessToken();
        }
    }

    public String getAccessToken() {
        Map<String, String> YD_CONFIG_MAP = dictDataService.getAllByDictType("yd_key");
        //Redis没有则发送请求获取AccessKey
        String key = YD_CONFIG_MAP.get("key");
        String value = YD_CONFIG_MAP.get("secret");
        String URL = GET_YD_KEY + "?accessKeyId=" + key + "&accessKeySecret=" + value;
        String response = HttpUtil.get(URL);
        JSONObject resData = JSONUtil.parseObj(response);
        log.info("获取AccessKey结果:" + response);
        if (resData.get("code").toString().equals("200")) {
            JSONObject info = JSONUtil.parseObj(resData.get("data"));
            String accessToken = info.get("accessToken").toString();
            String expiresIn = info.get("expiresIn").toString();
            log.info("获取AccessKey成功:" + accessToken);
            RedisUtils.setCacheObject("yd_key", accessToken, Duration.ofSeconds(Integer.parseInt(expiresIn) - 5));
            return accessToken;
        } else {
            throw new RuntimeException("获取AccessKey失败");
        }
    }

    public boolean runYD(String phone) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("robotClientGroupUuid", "7802b063-dcf7-4c05-bdcd-053702641362");
        map.put("robotUuid", "eb46a398-25cd-42a4-b1d2-26571d7fa7bc");
        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "phone");
        params.put("type", "str");
        params.put("value", phone);
        objects.add(params);
        map.put("params", objects);

        return RunYDWithTokenAndParam(map);
    }


    public boolean RunYD(String ydAppId, String ydRobotName, String ydRobotGroupId, String jsonParams) {
        HashMap<String, Object> map = new HashMap<>();
        if (StrUtil.isNotEmpty(ydRobotGroupId)) {
            map.put("robotClientGroupUuid", ydRobotGroupId);
        } else if (StrUtil.isNotEmpty(ydRobotName)) {
            map.put("accountName", ydRobotName);
        }
        map.put("robotUuid", ydAppId);
        map.put("waitTimeoutSeconds", 18000);

        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "params");
        params.put("type", "str");
        params.put("value", jsonParams);
        objects.add(params);
        map.put("params", objects);

        //获取accessKey
        return RunYDWithTokenAndParam(map);
    }

    private boolean RunYDWithTokenAndParam(HashMap<String, Object> map) {
        //获取accessKey
        String accessToken = getAccessTokenWithRedis();
        //构建请求头
        String authorization = "Bearer " + accessToken;
        //发送Post请求
        String response = HttpRequest.post(START_YD)
            .header("Authorization", authorization)
            .header("Content-Type", "application/json")
            .body(JSONUtil.toJsonStr(map))
            .execute()
            .body();
        log.info("请求结果:" + response);
        JSONObject resData = JSONUtil.parseObj(response);
        if (resData.get("code").toString().equals("200")) {
            return true;
        } else {
            String accessToken1 = getAccessToken();
            //构建请求头
            String authorization1 = "Bearer " + accessToken1;
            //发送Post请求
            String response1 = HttpRequest.post(START_YD)
                .header("Authorization", authorization1)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(map))
                .execute()
                .body();
            log.info("请求结果:" + response1);
            JSONObject resData1 = JSONUtil.parseObj(response1);
            return resData1.get("code").toString().equals("200");
        }
    }

    /*public boolean runXHSJieLiu(String ydAppId,String ydRobotName,String ydRobotGroupId, String jsonParams) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("accountName", ydRobotName);
        map.put("robotUuid", ydAppId);

        ArrayList<Object> objects = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("name", "params");
        params.put("type", "str");
        params.put("value", jsonParams);
        objects.add(params);
        map.put("params", objects);

        //获取accessKey
        String accessToken = getAccessToken();
        //构建请求头
        String authorization = "Bearer " + accessToken;
        //发送Post请求
        String response = HttpRequest.post(START_YD)
                .header("Authorization", authorization)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(map))
                .execute()
                .body();
        log.info("请求结果:" + response);
        JSONObject resData = JSONUtil.parseObj(response);
        return resData.get("code").toString().equals("200");
    }*/


}
