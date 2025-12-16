package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * AI内容风格对象 ai_prompt_style
 *
 * @author ZRL
 * @date 2025-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_prompt_style")
public class AiPromptStyle extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 风格名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 风格类型
     */
    private String type;
    /**
     * 提示词
     */
    private String prompt;
    /**
     * 排序
     */
    private Long orders;

    /**
     * 状态
     */
    private String enable;

}
