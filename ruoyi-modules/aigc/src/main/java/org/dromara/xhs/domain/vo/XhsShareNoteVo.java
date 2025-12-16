package org.dromara.xhs.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.xhs.domain.XhsShareNote;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * 小红书笔记链接分享视图对象 xhs_share_note
 *
 * @author ZRL
 * @date 2025-08-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = XhsShareNote.class)
public class XhsShareNoteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ExcelProperty(value = "id")
    private Long id;

    /**
     * 分享链接
     */
    @ExcelProperty(value = "分享链接")
    private String shareUrl;

    /**
     * 笔记id
     */
    @ExcelProperty(value = "笔记id")
    private String noteId;

    /**
     * 笔记链接类型
     */
    private String urlType;

    /**
     * 上次互动
     */
    @ExcelProperty(value = "上次互动")
    private Integer preInteraction;

    /**
     * 当前互动量
     */
    @ExcelProperty(value = "当前互动量")
    private Integer interaction;

    private String username;

    private String title;

    private String cover;

    private String avatar;

    @JsonFormat(pattern = "MM-dd HH:mm")
    private LocalDateTime postTime;


    /**
     * 是否监控 ON |  OFF
     */
    @ExcelProperty(value = "是否监控 ON |  OFF")
    private String monitor;

    @ExcelProperty(value = "创建者")
    private String createBy;

    @JsonFormat(pattern = "MM-dd HH:mm")
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    @JsonFormat(pattern = "MM-dd HH:mm")
    @ExcelProperty(value = "更新时间")
    private Date updateTime;


}
