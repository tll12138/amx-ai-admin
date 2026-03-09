package org.dromara.rp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * 文章任务明细对象 rp_article_detail
 *
 * @author ZRL
 * @date 2026-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_article_detail")
public class RpArticleDetail extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务明细ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 所属任务ID
     */
    private Long taskId;
    /**
     * 执行账号ID
     */
    private Long accountId;
    /**
     * 文章类型
     */
    private String type;
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章内容
     */
    private String content;
    /**
     * 发布状态
     */
    private Long publishStatus;
    /**
     * 额外信息（如视频URL、封面图、标签、发布时间等）
     */
    private String extraInfo;
    /**
     * 发布截图
     */
    private String screenshot;

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
    private Long ifBite;

    /**
     * 比特浏览器id
     */
    private Long biteNo;

    /**
     * 发布笔记id
     */
    private String noteId;

    /**
     * 发布截图
     */
    private String postScreenshot;

}
