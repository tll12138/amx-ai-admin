package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.util.List;

/**
 * 产品信息业务对象 ai_goods
 *
 * @author ZRL
 * @date 2025-03-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiGoods.class, reverseConvertGenerate = false, uses = ConvertStrAndList.class)
public class AiGoodsBo extends BaseDo {

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String name;

    /**
     * 品牌
     */
    @NotBlank(message = "品牌不能为空", groups = {AddGroup.class, EditGroup.class})
    private String brand;

    /**
     * 功效
     */
    private String effect;

    /**
     * 归属
     */
    @NotBlank(message = "归属不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

    /**
     * 年龄范围
     */
    private String ageRange;

    /**
     * 成分
     */
    private String component;


    /**
     * 卖点描述
     */
    private String description;


    /**
     * 绑定ID列表
     */
    private List<String> productIds;

    /**
     * 别名
     */
    @AutoMapping(target = "nickName", qualifiedByName = "List2Str")
    private List<String> nickName;

}
