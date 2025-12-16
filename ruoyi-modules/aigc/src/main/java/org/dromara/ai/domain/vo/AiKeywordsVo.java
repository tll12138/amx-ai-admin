package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiKeywords;
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
 * 关键词视图对象 ai_keywords
 *
 * @author ZRL
 * @date 2025-04-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiKeywords.class)
public class AiKeywordsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

    /**
     * 类别
     */
    @ExcelProperty(value = "类别")
    private String type;

    /**
     * 关键词
     */
    @ExcelProperty(value = "关键词")
    private String keyword;

    /**
     * 搜索指数
     */
    @ExcelProperty(value = "搜索指数")
    private Integer weight;


}
