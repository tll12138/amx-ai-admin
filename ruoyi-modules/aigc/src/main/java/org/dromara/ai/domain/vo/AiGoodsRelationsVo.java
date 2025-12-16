package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiGoodsRelations;
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
 * 商品竞品关系视图对象 ai_goods_relations
 *
 * @author ZRL
 * @date 2025-03-25
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiGoodsRelations.class)
public class AiGoodsRelationsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @ExcelProperty(value = "商品id")
    private String productId;

    /**
     * 竞品id
     */
    @ExcelProperty(value = "竞品id")
    private String competitorId;


}
