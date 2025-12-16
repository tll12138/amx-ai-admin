package org.dromara.rp.domain;

import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 文章任务明细对象 rp_article_detail
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_article_detail")
public class RpArticleDetail extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 任务明细ID
         */
        @TableId(value = "id", type = IdType.AUTO)
        private Long id;
        /**
         * 所属任务ID
         */
        private Long taskId;
        /**
         * 执行账号ID
         */
        private Long accountId;
        /**
         * 文章类型（image_text 图文 / video 视频）
         */
        private String type;
        /**
         * 文章标题
         */
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
