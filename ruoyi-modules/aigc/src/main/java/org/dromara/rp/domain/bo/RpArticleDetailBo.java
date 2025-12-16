package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.RpArticleDetail;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 文章任务明细业务对象 rp_article_detail
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpArticleDetail.class, reverseConvertGenerate = false)
public class RpArticleDetailBo extends BaseDo {

        /**
         * 任务明细ID
         */
        @NotNull(message = "任务明细ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 所属任务ID
         */
        @NotNull(message = "所属任务ID不能为空", groups = { AddGroup.class, EditGroup.class })
        private Long taskId;

        /**
         * 执行账号ID
         */
        @NotNull(message = "执行账号ID不能为空", groups = { AddGroup.class, EditGroup.class })
        private Long accountId;

        /**
         * 文章类型（image_text 图文 / video 视频）
         */
        @NotBlank(message = "文章类型（image_text 图文 / video 视频）不能为空", groups = { AddGroup.class, EditGroup.class })
        private String type;

        /**
         * 文章标题
         */
        @NotBlank(message = "文章标题不能为空", groups = { AddGroup.class, EditGroup.class })
        private String title;

        /**
         * 文章内容
         */
        private String content;

        /**
         * 发布状态（0未发布 1发布中 2成功 3失败）
         */
        private Long publishStatus;

        /**
         * 额外信息（如视频URL、封面图、标签、发布时间等）
         */
        private String extraInfo;


}
