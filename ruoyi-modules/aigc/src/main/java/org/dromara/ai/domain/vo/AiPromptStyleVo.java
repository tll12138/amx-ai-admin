package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiPromptStyle;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * AI内容风格视图对象 ai_prompt_style
 *
 * @author ZRL
 * @date 2025-04-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiPromptStyle.class)
public class AiPromptStyleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 风格名称
     */
    @ExcelProperty(value = "风格名称")
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 风格类型
     */
    @ExcelProperty(value = "风格类型")
    private String type;

    /**
     * 提示词
     */
    @ExcelProperty(value = "提示词")
    private String prompt;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long orders;

    /**
     * 状态
     */
    private String enable;

    /**
     * 创建人
     */
    private String createBy;

    private Date createTime;

    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "createBy")
    private String createByName;
}
