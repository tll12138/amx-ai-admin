package org.dromara.xhs.util.note;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * API请求参数模型
 *
 * @author ZRL
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {

    /**
     * 请求的URL
     */
    private String url;

    /**
     * 创建请求对象的便捷方法
     * @param url 请求URL
     * @return ApiRequest对象
     */
    public static ApiRequest of(String url) {
        return new ApiRequest(url);
    }
}
