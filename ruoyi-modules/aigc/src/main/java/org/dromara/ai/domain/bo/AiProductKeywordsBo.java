package org.dromara.ai.domain.bo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiProductKeywords;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.util.Date;

/**
 * 产品-关键词关系业务对象 ai_product_keywords
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@AutoMapper(target = AiProductKeywords.class, reverseConvertGenerate = false)
public class AiProductKeywordsBo {

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空", groups = {EditGroup.class})
    private String productId;

    /**
     * 关键词
     */
    @NotBlank(message = "关键词不能为空", groups = {AddGroup.class, EditGroup.class})
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
