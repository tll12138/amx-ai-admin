package org.dromara.xhs.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 小红书笔记链接分享对象 xhs_share_note
 *
 * @author ZRL
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xhs_share_note")
public class XhsShareNote implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分享链接
     */
    private String shareUrl;

    /**
     * 笔记id
     */
    private String noteId;

    /**
     * 笔记链接类型
     */
    private String urlType;

    /**
     * 上次互动
     */
    private Integer preInteraction;

    /**
     * 当前互动量
     */
    private Integer interaction;

    private String username;

    private String title;

    private String cover;

    private String avatar;

    private LocalDateTime postTime;


    /**
     * 是否监控 ON |  OFF
     */
    private String monitor;

    /**
     * 创建者XhsShareNote.getCreateBy()
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


    /**
     * 创建时间
     */
    private Date updateTime;


    /**
     * 逻辑删除
     */
    @TableLogic
    private Long deleted;
}
