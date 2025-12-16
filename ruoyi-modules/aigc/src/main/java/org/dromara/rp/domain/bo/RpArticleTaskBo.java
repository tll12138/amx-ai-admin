package org.dromara.rp.domain.bo;

import org.dromara.rp.domain.RpArticleTask;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 文章任务主业务对象 rp_article_task
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = RpArticleTask.class, reverseConvertGenerate = false)
public class RpArticleTaskBo extends BaseDo {

        /**
         * 任务ID
         */
        @NotNull(message = "任务ID不能为空", groups = { EditGroup.class })
        private Long id;

        /**
         * 任务名称
         */
        @NotBlank(message = "任务名称不能为空", groups = { AddGroup.class, EditGroup.class })
        private String taskName;

        /**
         * 任务描述
         */
        private String description;

        /**
         * 文章数量
         */
        private Long totalArticles;

        /**
         * 任务状态（0未开始 1进行中 2已完成 3失败）
         */
        private Long status;


}
