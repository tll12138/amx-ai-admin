package org.dromara.ai.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkValidator {

    // 淘宝链接正则
    private static final String TAOBAO_PATTERN = "^https?://(?:item\\.taobao\\.com|detail\\.tmall\\.(?:com|hk/hk))/item\\.htm\\?(?:.*&)?id=(\\d+)(?:&.*)?$";
    // 京东链接正则
    private static final String JD_PATTERN = "^https?://(?:item\\.jd\\.com|npcitem\\.jd\\.hk)/(\\d+)\\.html(?:\\?.*)?$";
    // 抖音链接正则
    private static final String DOUYIN_PATTERN = "^https?://haohuo\\.jinritemai\\.com/ecommerce/trade/detail/index\\.html\\?id=(\\d+)(?:&.*)?$";

    /**
     * 校验链接是否属于指定平台，并提取商品ID
     *
     * @param platform 平台名称（淘宝、京东、抖音）
     * @param url      待校验的链接
     * @return 如果是该平台的链接，返回商品ID；否则返回 null
     */
    public static String validateLink(String platform, String url) {
        if (platform == null || url == null || url.isEmpty()) {
            return null;
        }

        // 根据平台名称选择正则表达式
        String regex;
        switch (platform.toLowerCase()) {
            case "淘宝":
                regex = TAOBAO_PATTERN;
                break;
            case "京东":
                regex = JD_PATTERN;
                break;
            case "抖音":
                regex = DOUYIN_PATTERN;
                break;
            default:
                throw new IllegalArgumentException("不支持的平台: " + platform);
        }

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        // 校验链接并提取商品ID
        if (matcher.matches()) {
            return matcher.group(1); // 返回商品ID
        } else {
            return null; // 不是该平台的链接，返回空
        }
    }
}
