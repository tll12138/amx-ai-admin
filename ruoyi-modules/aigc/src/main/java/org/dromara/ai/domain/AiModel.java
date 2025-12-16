package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * AI模型配置对象 ai_model
 *
 * @author ZRL
 * @date 2025-04-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_model")
public class AiModel extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 模型名称
     */
    private String model;
    /**
     * 供应商
     */
    private String provider;
    /**
     * 模型别名
     */
    private String name;
    /**
     * 最大回复长度
     */
    private Integer maxToken;
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
    private String apiKey;
    /**
     * 超时时间（分钟）
     */
    private Integer timeout;
    /**
     * baseUrl
     */
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

    private String modelType;

    /**
     * 状态
     */
    private String enable;
}
