package org.dromara.ai.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author zrl
 * @date 2025/5/7
 */
@ExcelIgnoreUnannotated
@Data
public class AiCommentExportVo {

    @ExcelProperty(value = "产品名称")
    private String name;

    @ExcelProperty(value = "评论内容")
    private String comment;
}
