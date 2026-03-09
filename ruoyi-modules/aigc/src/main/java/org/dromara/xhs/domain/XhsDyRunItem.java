package org.dromara.xhs.domain;

import lombok.Data;

import java.util.List;

@Data
public class XhsDyRunItem {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 0.图片 1.视频
     */
    private Integer type;

    /**
     * 图片/视频地址
     */
    private List<String> medium;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签
     */
    private List<String> tagList;

    /**
     * 定时
     */
    private String schedule;

    /**
     * 分组名称
     */
    private String group;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 提及@
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
    private Long isBite;

    /**
     * 比特浏览器编号
     */
    private String biteId;
}
