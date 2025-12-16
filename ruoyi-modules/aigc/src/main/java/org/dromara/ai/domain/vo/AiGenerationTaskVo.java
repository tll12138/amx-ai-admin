package org.dromara.ai.domain.vo;

import cn.hutool.core.collection.CollectionUtil;
import io.github.linpeilie.annotations.AutoMapping;
import org.dromara.ai.domain.AiGenerationTask;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * AI生成任务视图对象 ai_generation_task
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiGenerationTask.class, uses = ConvertStrAndList.class)
public class AiGenerationTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    private String id;

    /**
     * 创作风格
     */
    @ExcelProperty(value = "创作风格", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "ai_generate_style")
    private String style;

    /**
     * 模型
     */
    @ExcelProperty(value = "模型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "ai_model_config")
    private String model;

    /**
     * 笔记ID
     */
    @ExcelProperty(value = "笔记ID")
    private String noteId;

    /**
     * 笔记ID
     */
    @ExcelProperty(value = "笔记标题")
    private String noteTitle;

    /**
     * 笔记token
     */
    @ExcelProperty(value = "笔记token")
    private String xsecToken;

    /**
     * 笔记封面
     */
    @ExcelProperty(value = "笔记封面")
    private String noteCover;

    /**
     * 笔记内容
     */
    @ExcelProperty(value = "笔记内容")
    private String noteContent;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private String productId;

    /**
     * 竞品ID列表
     */
    @ExcelProperty(value = "竞品ID列表")
    private String competitorIds;

    /**
     * 埋词内容
     */
    @AutoMapping(target = "keywords", qualifiedByName = "List2Str")
    private List<String> keywords;

    /**
     * 参考纬度
     */
    @ExcelProperty(value = "参考纬度")
    private String referenceLatitude;

    /**
     * 字数限制
     */
    @ExcelProperty(value = "字数限制")
    private String otherLimit;

    /**
     * AI内容
     */
    @ExcelProperty(value = "AI内容")
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

    /**
     * 生成质量评分
     */
    @ExcelProperty(value = "生成质量评分")
    private Long grade;
}
