package org.dromara.rp.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * @author tll
 * @date 2025-12-22 16:46:37
 */
@Data
public class RpContentGroupInfo {
    /**
     * 媒体类型 0-图片 1-视频
     */
    private String type;

    /**
     * 视频地址（图片类型为空）
     */
    private String video;

    /**
     * 图片地址列表
     */
    private List<String> picList;

    /**
     * 发布时间（为空表示立即发布）
     */
    private String publishTime;

    /**
     * 笔记标题
     */
    private String title;

    /**
     * 笔记正文内容
     */
    private String content;

    /**
     * 选中的标签列表
     */
    private List<String> selectedTags;

    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 选中的发布账号ID
     */
    private Long accountId;

    /**
     * 发布后笔记的链接
     */
    private String url;

    /**
     * 额外信息（如视频URL、封面图、标签、发布时间等）
     */
    private String extraInfo;

    /**
     * 所属任务ID
     */
    private Long taskId;

    /**
     * 笔记中@的人
     */
    private String mention;

    /**
     * 是否控评
     */
    private String ifControlEvaluation;

    /**
     * 控评内容
     */
    private String controlEvaluationContent;

    /**
     * 是否比特
     */
    private String ifBite;

    /**
     * 比特浏览器id
     */
    private String biteNo;
}
