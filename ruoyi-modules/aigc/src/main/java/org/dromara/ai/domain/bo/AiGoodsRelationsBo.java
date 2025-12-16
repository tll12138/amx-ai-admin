package org.dromara.ai.domain.bo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.dromara.ai.domain.AiGoodsRelations;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;

import java.util.Date;

/**
 * 商品竞品关系业务对象 ai_goods_relations
 *
 * @author ZRL
 * @date 2025-03-25
 */
@Data
@AutoMapper(target = AiGoodsRelations.class, reverseConvertGenerate = false)
public class AiGoodsRelationsBo {

    /**
     * 商品id
     */
    @NotBlank(message = "商品id不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productId;

    /**
     * 竞品id
     */
    @NotBlank(message = "竞品id不能为空", groups = {AddGroup.class, EditGroup.class})
    private String competitorId;

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
