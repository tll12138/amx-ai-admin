package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiPromptStyle;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

/**
 * AI内容风格业务对象 ai_prompt_style
 *
 * @author ZRL
 * @date 2025-04-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiPromptStyle.class, reverseConvertGenerate = false)
public class AiPromptStyleBo extends BaseDo {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 风格名称
     */
    @NotBlank(message = "风格名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    private String description;

    /**
     * 风格类型
     */
    private String type;

    /**
     * 提示词
     */
    @NotBlank(message = "提示词不能为空", groups = {AddGroup.class, EditGroup.class})
    private String prompt;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = {AddGroup.class})
    private Long orders;

    /**
     * 状态
     */
    private String enable;


}
