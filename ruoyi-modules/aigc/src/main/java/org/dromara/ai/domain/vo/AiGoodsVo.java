package org.dromara.ai.domain.vo;

import io.github.linpeilie.annotations.AutoMapping;
import org.dromara.ai.domain.AiGoods;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 产品信息视图对象 ai_goods
 *
 * @author ZRL
 * @date 2025-03-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiGoods.class, uses = ConvertStrAndList.class)
public class AiGoodsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private String id;

    /**
     * 产品名称
     */
    @ExcelProperty(value = "产品名称")
    private String name;

    /**
     * 品牌
     */
    @ExcelProperty(value = "品牌")
    private String brand;

    /**
     * 功效
     */
    @ExcelProperty(value = "功效")
    private String effect;

    /**
     * 归属
     */
    @ExcelProperty(value = "归属", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "product_type")
    private String type;

    /**
     * 年龄范围
     */
    @ExcelProperty(value = "年龄")
    private String ageRange;

    /**
     * 成分
     */
    @ExcelProperty(value = "成分")
    private String component;

    /**
     * 卖点描述
     */
    @ExcelProperty(value = "卖点")
    private String description;

    private List<String> productIds;

    /**
     * 创建人
     */
    private String createBy;

    private Date createTime;

    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "createBy")
    private String createByName;

    /**
     * 别名
     */
    @ExcelProperty(value = "别名")
    @AutoMapping(target = "nickName", qualifiedByName = "List2Str")
    private List<String> nickName;
}
