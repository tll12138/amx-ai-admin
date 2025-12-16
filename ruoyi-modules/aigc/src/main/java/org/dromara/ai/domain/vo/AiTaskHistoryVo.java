package org.dromara.ai.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.ai.domain.AiGenerationTask;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;
import java.io.Serializable;
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
public class AiTaskHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    private String id;

    /**
     * 任务类型
     */
    private String type;

    /**
     * 创作风格
     */
    private String styleName;

    /**
     * 模型
     */
    private String modelName;

    /**
     * 笔记ID
     */
    private String noteId;

    /**
     * 笔记ID
     */
    private String noteTitle;

    /**
     * 笔记token
     */
    private String xsecToken;

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
    private String productName;

    /**
     * 竞品ID列表
     */
    private List<String> competitors;

    /**
     * 参考纬度
     */
    private String referenceLatitude;

    /**
     * 字数限制
     */
    private String otherLimit;

    /**
     * AI内容
     */
    private String aiContent;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 生成状态
     */
    private String generateStatus;

    /**
     * 生成质量评分
     */
    private Long grade;


    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 更新时间
     */
    private Date updateTime;


}
