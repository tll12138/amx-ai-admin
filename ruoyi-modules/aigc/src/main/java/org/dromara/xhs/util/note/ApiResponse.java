package org.dromara.xhs.util.note;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * API响应数据模型
 *
 * @author ZRL
 */
@Data
public class ApiResponse {

    /**
     * 响应状态码，0表示成功，1表示失败
     */
    private Integer code;

    /**
     * 收藏数
     */
    @JsonProperty("collect_num")
    private String collectNum;

    /**
     * 评论数
     */
    @JsonProperty("comment_num")
    private String commentNum;

    /**
     * 封面图片URL
     */
    @JsonProperty("cover_img")
    private String coverImg;

    /**
     * 点赞数
     */
    @JsonProperty("like_num")
    private String likeNum;

    /**
     * 用户ID
     */
    @JsonProperty("nick_id")
    private String nickId;

    /**
     * 用户昵称
     */
    @JsonProperty("nick_name")
    private String nickName;

    /**
     * 用户头像URL
     */
    @JsonProperty("nick_pic")
    private String nickPic;

    /**
     * 用户令牌
     */
    @JsonProperty("nick_token")
    private String nickToken;

    /**
     * 发布时间戳
     */
    @JsonProperty("push_time")
    private Long pushTime;

    /**
     * 分享数
     */
    @JsonProperty("share_num")
    private String shareNum;

    /**
     * 标题
     */
    private String title;

    /**
     * 判断响应是否成功
     * @return true表示成功，false表示失败
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }

    /**
     * 判断响应是否失败
     * @return true表示失败，false表示成功
     */
    public boolean isFailed() {
        return !isSuccess();
    }
}
