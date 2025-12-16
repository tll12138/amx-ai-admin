package org.dromara.xhs.domain.bo;

import org.dromara.xhs.domain.XhsShareRecord;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 分享记录业务对象 xhs_share_record
 *
 * @author ZRL
 * @date 2025-07-24
 */
@Data
@EqualsAndHashCode(callSuper = true)

public class XhsShareRecordBo extends BaseDo {

        /**
         * 分享记录ID
         */
        @NotNull(message = "分享记录ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 分享唯一标识
         */
        @NotBlank(message = "分享唯一标识不能为空", groups = { AddGroup.class, EditGroup.class })
        private String shareId;

        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 标题
         */
        @NotBlank(message = "标题不能为空", groups = { AddGroup.class, EditGroup.class })
        private String title;

        /**
         * 内容
         */
        @NotBlank(message = "内容不能为空", groups = { AddGroup.class, EditGroup.class })
        private String content;

        /**
         * 图片列表（图文）
         */
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
        @NotBlank(message = "分享类型：normal-图文，video-视频不能为空", groups = { AddGroup.class, EditGroup.class })
        private String shareType;

        /**
         * 状态：0-已删除，1-正常，2-已过期
         */
        @NotNull(message = "状态：0-已删除，1-正常，2-已过期不能为空", groups = { AddGroup.class, EditGroup.class })
        private Long status;

        /**
         * 过期时间
         */
        private Date expiresAt;

        /**
         * 访问次数
         */
        @NotNull(message = "访问次数不能为空", groups = { AddGroup.class, EditGroup.class })
        private Long accessCount;

        /**
         * 最近访问时间
         */
        @NotNull(message = "最近访问时间不能为空", groups = { AddGroup.class, EditGroup.class })
        private Long accessTime;


}
