package org.dromara.ai.domain.vo;

import org.dromara.ai.domain.AiModel;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * AI模型配置视图对象 ai_model
 *
 * @author ZRL
 * @date 2025-04-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiModel.class)
public class AiModelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private String id;

    /**
     * 模型名称
     */
    @ExcelProperty(value = "模型名称")
    private String model;

    /**
     * 供应商
     */
    @ExcelProperty(value = "供应商")
    private String provider;

    /**
     * 模型别名
     */
    @ExcelProperty(value = "模型别名")
    private String name;

    /**
     * 最大回复长度
     */
    @ExcelProperty(value = "最大回复长度")
    private Integer maxToken;

    /**
     * 温度
     */
    @ExcelProperty(value = "温度")
    private Double temperature;

    /**
     * top_p
     */
    @ExcelProperty(value = "top_p")
    private Double topP;

    /**
     * 超时时间（分钟）
     */
    @ExcelProperty(value = "超时时间", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "分=钟")
    private Integer timeout;

    /**
     * baseUrl
     */
    @ExcelProperty(value = "baseUrl")
    private String baseUrl;

    /**
     * endpoint
     */
    @ExcelProperty(value = "endpoint")
    private String endpoint;

    /**
     * apiKey
     */
    private String apiKey;
    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 排序
     */
    private Integer orders;


    private String modelType;


    /**
     * 状态
     */
    private String enable;

    /**
     * 创建人
     */
    private String createBy;

    private Date createTime;

    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "createBy")
    private String createByName;

}
