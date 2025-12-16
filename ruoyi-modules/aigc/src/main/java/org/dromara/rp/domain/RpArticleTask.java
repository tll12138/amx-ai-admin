package org.dromara.rp.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * 文章任务主对象 rp_article_task
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rp_article_task")
public class RpArticleTask extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 平台
     */
    private String platform;
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
