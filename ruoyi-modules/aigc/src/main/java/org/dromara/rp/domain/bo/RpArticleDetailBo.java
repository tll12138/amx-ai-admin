package org.dromara.rp.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.rp.domain.RpArticleDetail;

/**
 * 文章任务明细业务对象 rp_article_detail
 *
 * @author ZRL
 * @date 2026-02-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpArticleDetail.class, reverseConvertGenerate = false)
public class RpArticleDetailBo extends BaseDo {

    /**
     * 任务明细ID
     */
    @NotNull(message = "任务明细ID不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 所属任务ID
     */
    @NotNull(message = "所属任务ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long taskId;

    /**
     * 执行账号ID
     */
    @NotNull(message = "执行账号ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long accountId;

    /**
     * 文章类型
     */
    @NotBlank(message = "文章类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空", groups = {AddGroup.class, EditGroup.class})
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
