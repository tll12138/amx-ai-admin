package org.dromara.rp.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.rp.domain.RpArticleDetail;

import java.io.Serial;
import java.io.Serializable;



/**
 * 文章任务明细视图对象 rp_article_detail
 *
 * @author ZRL
 * @date 2026-02-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpArticleDetail.class)
public class RpArticleDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务明细ID
     */
    @ExcelProperty(value = "任务明细ID")
    private Long id;

    /**
     * 所属任务ID
     */
    @ExcelProperty(value = "所属任务ID")
    private Long taskId;

    /**
     * 执行账号ID
     */
    @ExcelProperty(value = "执行账号ID")
    private Long accountId;

    /**
     * 执行账号名称
     */
    @ExcelProperty(value = "执行账号")
    private String accountName;

    /**
     * 文章类型
     */
    @ExcelProperty(value = "文章类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "article_type")
    private String type;

    /**
     * 文章标题
     */
    @ExcelProperty(value = "文章标题")
    private String title;

    /**
     * 文章内容
     */
    @ExcelProperty(value = "文章内容")
    private String content;

    /**
     * 发布状态
     */
    @ExcelProperty(value = "发布状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "article_release_status")
    private Long publishStatus;

    /**
     * 额外信息（如视频URL、封面图、标签、发布时间等）
     */
    @ExcelProperty(value = "额外信息", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=视频URL、封面图、标签、发布时间等")
    private String extraInfo;

    /**
     * 发布截图
     */
    @ExcelProperty(value = "发布截图")
    private String screenshot;

    /**
     * 发布截图Url
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "screenshot")
    private String screenshotUrl;

    /**
     * 发布时间
     */
    @ExcelProperty(value = "发布时间")
    private String updateTime;

    /**
     * 是否控评
     */
    @ExcelProperty(value = "是否控评", converter = ExcelDictConvert.class)
    private String ifControlEvaluation;

    /**
     * 控评内容
     */
    @ExcelProperty(value = "控评内容")
    private String controlEvaluationContent;

    /**
     * 是否比特
     */
    @ExcelProperty(value = "是否比特", converter = ExcelDictConvert.class)
    private Long ifBite;

    /**
     * 比特浏览器id
     */
    @ExcelProperty(value = "比特浏览器id")
    private Long biteNo;

    /**
     * 发布笔记id
     */
    @ExcelProperty(value = "发布笔记id")
    private String noteId;

    /**
     * 发布截图
     */
    @ExcelProperty(value = "发布截图")
    private String postScreenshot;

}
