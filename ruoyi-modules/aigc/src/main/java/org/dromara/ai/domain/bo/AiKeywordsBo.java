package org.dromara.ai.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiKeywords;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.util.List;

/**
 * 关键词业务对象 ai_keywords
 *
 * @author ZRL
 * @date 2025-04-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiKeywords.class, reverseConvertGenerate = false)
public class AiKeywordsBo extends BaseDo {

    /**
     * ID
     */
    @NotBlank(message = "ID不能为空", groups = {EditGroup.class})
    private String id;

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空", groups = {AddGroup.class, EditGroup.class})
    private String productId;

    /**
     * 类别
     */
    @NotBlank(message = "类别不能为空", groups = {AddGroup.class, EditGroup.class})
    private String type;

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
     * 别名
     */
    @ExcelProperty(value = "别名")
    private List<String> nickName;
}
