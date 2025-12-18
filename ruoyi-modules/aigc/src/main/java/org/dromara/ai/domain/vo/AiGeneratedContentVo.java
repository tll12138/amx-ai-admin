package org.dromara.ai.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.ai.domain.AiGeneratedContent;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * AI生成内容解析视图对象 ai_generated_content
 *
 * @author LL
 * @date 2025-12-18
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiGeneratedContent.class)
public class AiGeneratedContentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private String id;

    /**
     * 关联的生成任务ID
     */
    @ExcelProperty(value = "关联的生成任务ID")
    private String taskId;

    /**
     * 原始AI生成内容
     */
    @ExcelProperty(value = "原始AI生成内容")
    private String originalContent;

    /**
     * 解析后的标题
     */
    @ExcelProperty(value = "解析后的标题")
    private String parsedTitle;

    /**
     * 解析后的段落（JSON格式）
     */
    @ExcelProperty(value = "解析后的段落", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式")
    private String parsedParagraphs;

    /**
     * 关键词分析（JSON格式）
     */
    @ExcelProperty(value = "关键词分析", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "J=SON格式")
    private String keywordAnalysis;

    /**
     * 生成时间
     */
    @ExcelProperty(value = "生成时间")
    private Date generateTime;


}
