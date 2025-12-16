package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiProductKeywords;
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
 * 产品-关键词关系视图对象 ai_product_keywords
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiProductKeywords.class)
public class AiProductKeywordsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称")
    private String productName;

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
