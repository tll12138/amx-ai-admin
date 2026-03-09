package org.dromara.rp.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;
import org.dromara.rp.domain.RpAccount;
import org.dromara.rp.domain.RpArticleTask;

import java.util.List;

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

        /**
         * 平台标识
         */
        @NotBlank(message = "平台标识不能为空", groups = { EditGroup.class })
        private String platform;

        /**
         * 账号列表
         */
        private List<RpAccount> accounts;

        /**
         * 发布内容主体
         */
        private RpContentInfo content;

        /**
         * 终端类型
         */
        private Long deviceType = 0L;


}
