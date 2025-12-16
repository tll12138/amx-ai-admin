package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiModel;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

/**
 * AI模型配置业务对象 ai_model
 *
 * @author ZRL
 * @date 2025-04-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiModel.class, reverseConvertGenerate = false)
public class AiModelBo extends BaseDo {

    /**
     * 主键
     */
    @NotBlank(message = "主键不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 模型名称
     */
    @NotBlank(message = "模型名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String model;

    /**
     * 供应商
     */
    @NotBlank(message = "供应商不能为空", groups = {AddGroup.class, EditGroup.class})
    private String provider;

    /**
     * 模型别名
     */
    @NotBlank(message = "模型别名不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 最大回复长度
     */
    @NotNull(message = "最大回复长度不能为空", groups = {AddGroup.class, EditGroup.class})
    private Long maxToken;

    /**
     * 温度
     */
    private Double temperature;

    /**
     * top_p
     */
    private Double topP;

    /**
     * apiKey
     */
    @NotBlank(message = "apiKey不能为空", groups = {AddGroup.class, EditGroup.class})
    private String apiKey;

    /**
     * 超时时间（分钟）
     */
    private Long timeout;

    /**
     * baseUrl
     */
    @NotBlank(message = "baseUrl不能为空", groups = {AddGroup.class, EditGroup.class})
    private String baseUrl;

    /**
     * endpoint
     */
    private String endpoint;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 排序
     */
    private Integer orders;

    /**
     * 状态
     */
    private String enable;

    private String modelType;

}
