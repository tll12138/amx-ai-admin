package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品-关键词关系对象 ai_product_keywords
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_product_keywords")
public class AiProductKeywords implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private String productId;
    /**
     * 关键词
     */
    private String keyword;
    /**
     * 搜索指数
     */
    private Integer weight;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
