package org.dromara.xhs.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.ai.domain.vo.AiGenerationTaskVo;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.xhs.domain.vo.share.XhsShareRecordVo;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 分享记录对象 xhs_share_record
 *
 * @author ZRL
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xhs_share_record")
@AutoMapper(target = XhsShareRecordVo.class, uses = ConvertStrAndList.class)
public class XhsShareRecord extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分享记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 分享唯一标识
     */
    private String shareId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容
     */
    private String content;
    /**
     * 图片列表（图文）
     */
    @AutoMapping(target = "images", qualifiedByName = "Str2List")
    private String images;
    /**
     * 视频
     */
    private String video;
    /**
     * 视频封面
     */
    private String cover;
    /**
     * 分享类型：normal-图文，video-视频
     */
    @NotNull(message = "分享类型不能为空")
    private String type;
    /**
     * 状态：0-已删除，1-正常，2-已过期
     */
    private Integer status;
    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;
    /**
     * 访问次数
     */
    private Integer accessCount;
    /**
     * 最近访问时间
     */
    private LocalDateTime accessTime;

}
