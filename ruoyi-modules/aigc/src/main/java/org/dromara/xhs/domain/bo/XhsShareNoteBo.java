package org.dromara.xhs.domain.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.xhs.domain.XhsShareNote;

import java.util.Date;

/**
 * 小红书笔记链接分享业务对象 xhs_share_note
 *
 * @author ZRL
 * @date 2025-08-15
 */
@Data
@AutoMapper(target = XhsShareNote.class, reverseConvertGenerate = false)
public class XhsShareNoteBo {

    /**
     * id
     */
    @NotNull(message = "id不能为空", groups = {EditGroup.class})
    private Long id;

    /**
     * 分享链接
     */
    @NotBlank(message = "分享链接不能为空", groups = {AddGroup.class, EditGroup.class})
    @ExcelProperty(value = "分享链接")
    private String shareUrl;
}
