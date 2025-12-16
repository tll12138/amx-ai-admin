package org.dromara.rp.domain.vo;

import org.dromara.rp.domain.RpArticleDetail;
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
 * 文章任务明细视图对象 rp_article_detail
 *
 * @author ZRL
 * @date 2025-11-13
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
     * 文章类型（image_text 图文 / video 视频）
     */
    @ExcelProperty(value = "文章类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "i=mage_text,图=文,/=,v=ideo,视=频")
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
     * 发布状态（0未发布 1发布中 2成功 3失败）
     */
    @ExcelProperty(value = "发布状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=未发布,1=发布中,2=成功,3=失败")
    private Long publishStatus;

    /**
     * 额外信息（如视频URL、封面图、标签、发布时间等）
     */
    @ExcelProperty(value = "额外信息", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=视频URL、封面图、标签、发布时间等")
    private String extraInfo;


}
