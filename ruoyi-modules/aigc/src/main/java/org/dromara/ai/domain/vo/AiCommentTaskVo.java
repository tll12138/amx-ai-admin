package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiCommentTask;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * AI评论生成视图对象 ai_comment_task
 *
 * @author ZRL
 * @date 2025-05-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiCommentTask.class)
public class AiCommentTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 生成类型
     */
    @ExcelProperty(value = "生成类型")
    private String type;

    /**
     * 风格内容
     */
    @ExcelProperty(value = "风格内容")
    private String style;

    /**
     * 模型
     */
    @ExcelProperty(value = "模型")
    private String model;

    /**
     * 示例内容
     */
    @ExcelProperty(value = "示例内容")
    private String example;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private String productId;

    /**
     * 情感倾向
     */
    @ExcelProperty(value = "情感倾向")
    private String sentiment;

    /**
     * 引导方向
     */
    @ExcelProperty(value = "引导方向")
    private String guideline;

    /**
     * 关键词
     */
    @ExcelProperty(value = "关键词")
    private String keywords;

    /**
     * 生成数量
     */
    @ExcelProperty(value = "生成数量")
    private Long commentCount;

    /**
     * 长度限制
     */
    @ExcelProperty(value = "长度限制")
    private Long maxWords;

    /**
     * 生成内容
     */
    @ExcelProperty(value = "生成内容")
    private String aiContent;

    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型")
    private String operationType;

    /**
     * 生成状态
     */
    @ExcelProperty(value = "生成状态")
    private String generateStatus;


}
