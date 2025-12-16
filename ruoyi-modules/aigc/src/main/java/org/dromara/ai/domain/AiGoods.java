package org.dromara.ai.domain;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.ai.domain.vo.AiGoodsVo;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * 产品信息对象 ai_goods
 *
 * @author ZRL
 * @date 2025-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_goods")
@AutoMapper(target = AiGoodsVo.class, uses = ConvertStrAndList.class)
public class AiGoods extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 产品名称
     */
    private String name;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 功效
     */
    private String effect;
    /**
     * 归属
     */
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
     * 别名
     */
    @AutoMapping(target = "nickName", qualifiedByName = "Str2List")
    private String nickName;

    @Override
    public String toString() {
        return "{" +
            "名称：'" + (StrUtil.isEmpty(nickName) ? name : nickName) + '\'' +
            ", 品牌：'" + brand + '\'' +
            ", 功效：'" + effect + '\'' +
            ", 适用范围：'" + ageRange + '\'' +
            ", 成分：'" + component + '\'' +
            ", 卖点：'" + description + '\'' +
            '}';
    }

}
