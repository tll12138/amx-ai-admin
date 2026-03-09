package org.dromara.rp.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.rp.domain.RpArticleTask;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 文章任务主视图对象 rp_article_task
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpArticleTask.class)
public class RpArticleTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    private Long id;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    private String taskName;

    /**
     * 任务描述
     */
    @ExcelProperty(value = "任务描述")
    private String description;

    /**
     * 文章数量
     */
    @ExcelProperty(value = "文章数量")
    private Long totalArticles;

    /**
     * 任务状态（0未开始 1进行中 2已完成 3失败）
     */
    @ExcelProperty(value = "任务状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=未开始,1=进行中,2=已完成,3=失败")
    private Long status;

    /**
     * 任务详情
     */
    private List<RpArticleDetailVo> detailVos;

    /**
     * 终端类型
     */
    @ExcelProperty(value = "终端类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_type")
    private Long deviceType;


}
