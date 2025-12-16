package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiGenerationTask;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.ai.enums.TaskTypeEnum;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.util.List;

/**
 * AI生成任务业务对象 ai_generation_task
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiGenerationTask.class, reverseConvertGenerate = false, uses = ConvertStrAndList.class)
public class AiGenerationTaskBo extends BaseDo {

    /**
     * 任务ID
     */
    @NotBlank(message = "任务ID不能为空", groups = {EditGroup.class})
    private String id;


    /**
     * 任务类型
     */
    private TaskTypeEnum type;

    /**
     * 创作风格
     */
    @NotBlank(message = "创作风格不能为空", groups = {AddGroup.class})
    private String style;

    /**
     * 模型
     */
    @NotBlank(message = "模型不能为空", groups = {AddGroup.class})
    private String model;


    /**
     * 笔记ID
     */
    private String noteId;

    /**
     * 笔记token
     */

    private String xsecToken;


    private String noteTitle;

    /**
     * 笔记封面
     */
    private String noteCover;

    /**
     * 笔记内容
     */
    private String noteContent;

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 竞品ID列表
     */
    private String competitorIds;

    /**
     * 参考纬度
     */
    private String referenceLatitude;

    /**
     * 其他要求
     */
    private String otherLimit;

    /**
     * AI内容
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
    @NotBlank(message = "生成状态不能为空", groups = {AddGroup.class})
    private String generateStatus;

    /**
     * 生成质量评分
     */
    private Long grade;

    /**
     * 埋词内容
     */
    @AutoMapping(target = "keywords", qualifiedByName = "List2Str")
    private List<String> keywords;

    /**
     * 模型随机性
     */
    private Double temperature;

    /**
     * 模型多样性
     */
    private Double topP;
}
