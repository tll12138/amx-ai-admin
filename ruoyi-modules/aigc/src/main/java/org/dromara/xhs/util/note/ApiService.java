package org.dromara.xhs.util.note;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * API服务类
 * 提供高级API调用功能和业务逻辑封装
 *
 * @author ZRL
 */
@Slf4j
public class ApiService {

    /**
     * 默认API接口地址（需要根据实际情况配置）
     */
    private static final String DEFAULT_API_URL = "https://xhs.puqi.group:8988/note_monitor";

    /**
     * 调用API解析URL（使用默认接口地址）
     *
     * @param url 要解析的URL
     * @return API响应结果
     */
    public static ApiResponse parseUrl(String url) {
        return parseUrl(DEFAULT_API_URL, url);
    }

    /**
     * 调用API解析URL（指定接口地址）
     *
     * @param apiUrl 接口地址
     * @param url 要解析的URL
     * @return API响应结果
     */
    public static ApiResponse parseUrl(String apiUrl, String url) {
        // 参数验证
        if (!StringUtils.hasText(apiUrl)) {
            log.error("API接口地址不能为空");
            return createErrorResponse("API接口地址不能为空");
        }

        if (!StringUtils.hasText(url)) {
            log.error("要解析的URL不能为空");
            return createErrorResponse("要解析的URL不能为空");
        }

        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // 创建请求对象
                ApiRequest request = ApiRequest.of(url);

                // 发送POST请求
                ApiResponse response = HttpClientUtil.sendPostRequest(apiUrl, request);

                // 记录结果
                if (response.isSuccess()) {
                    log.info("URL解析成功: {} -> 标题: {}, 作者: {}", url, response.getTitle(), response.getNickName());
                    return response;
                } else {
                    log.warn("URL解析失败: {}, 错误码: {}, 尝试次数: {}/{}", url, response.getCode(), attempt, maxRetries);
                    if (attempt == maxRetries) {
                        return response;
                    }
                }

                // 等待一段时间再重试
                Thread.sleep(1000);

            } catch (Exception e) {
                log.error("调用API解析URL时发生异常: {}, 尝试次数: {}/{}", url, attempt, maxRetries, e);
                if (attempt == maxRetries) {
                    return createErrorResponse("调用API时发生异常: " + e.getMessage());
                }

                // 等待一段时间再重试
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return createErrorResponse("请求被中断");
                }
            }
        }

        return createErrorResponse("请求失败，已达到最大重试次数");
    }

    /**
     * 批量解析URL
     *
     * @param urls URL列表
     * @return API响应结果列表
     */
    public static List<ApiResponse> parseUrls(List<String> urls) {
        List<ApiResponse> responses = new java.util.ArrayList<>();

        if (CollectionUtil.isEmpty(urls)) {
            log.warn("URL列表为空");
            return responses;
        }

        for (String url : urls) {
            try {
                ApiResponse response = parseUrl(url);
                responses.add(response);

                // 添加延迟以避免请求过于频繁
                Thread.sleep(100);

            } catch (InterruptedException e) {
                log.warn("批量处理被中断");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("处理URL时发生异常: {}", url, e);
                responses.add(createErrorResponse("处理异常: " + e.getMessage()));
            }
        }

        return responses;
    }

    /**
     * 验证API响应数据的完整性
     *
     * @param response API响应
     * @return 是否数据完整
     */
    public static boolean isDataComplete(ApiResponse response) {
        if (response == null || response.isFailed()) {
            return false;
        }

        // 检查关键字段是否存在
        return StringUtils.hasText(response.getTitle()) &&
               StringUtils.hasText(response.getNickName()) &&
               StringUtils.hasText(response.getCoverImg());
    }

    /**
     * 创建错误响应
     *
     * @param errorMessage 错误信息
     * @return 错误响应对象
     */
    private static ApiResponse createErrorResponse(String errorMessage) {
        ApiResponse response = new ApiResponse();
        response.setCode(1);
        response.setTitle(errorMessage);
        return response;
    }

    /**
     * 设置默认API地址（用于配置）
     *
     * @param apiUrl 新的默认API地址
     */
    public static void setDefaultApiUrl(String apiUrl) {
        // 这里可以实现动态配置默认API地址的逻辑
        log.info("设置默认API地址: {}", apiUrl);
    }
}
