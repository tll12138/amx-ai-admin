package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiCommentTask;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * AI评论生成业务对象 ai_comment_task
 *
 * @author ZRL
 * @date 2025-05-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiCommentTask.class, reverseConvertGenerate = false)
public class AiCommentTaskBo extends BaseEntity {

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 生成类型
     */
    @NotBlank(message = "生成类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

    /**
     * 风格内容
     */
    @NotBlank(message = "风格内容不能为空", groups = {AddGroup.class, EditGroup.class})
    private String style;

    /**
     * 模型
     */
    @NotBlank(message = "模型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String model;

    /**
     * 示例内容
     */
    private String example;

    /**
     * 产品ID
     */
    @NotBlank(message = "产品ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productId;

    /**
     * 情感倾向
     */
    private String sentiment;

    /**
     * 引导方向
     */
    private String guideline;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 生成数量
     */
    @NotNull(message = "生成数量不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long commentCount;

    /**
     * 长度限制
     */
    @NotNull(message = "长度限制不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long maxWords;

    /**
     * 生成内容
     */
    private String aiContent;

    /**
     * 操作类型
     */
    @NotBlank(message = "操作类型不能为空", groups = {AddGroup.class, EditGroup.class})
    private String operationType;

    /**
     * 生成状态
     */
    @NotBlank(message = "生成状态不能为空", groups = {AddGroup.class, EditGroup.class})
    private String generateStatus;

    /**
     * 模型随机性
     */
    private Double temperature;

    /**
     * 模型多样性
     */
    private Double topP;

}
