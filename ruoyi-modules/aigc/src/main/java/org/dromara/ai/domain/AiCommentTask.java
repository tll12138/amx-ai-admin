package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;

/**
 * AI评论生成对象 ai_comment_task
 *
 * @author ZRL
 * @date 2025-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_comment_task")
public class AiCommentTask extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 生成类型
     */
    private String type;
    /**
     * 风格内容
     */
    private String style;
    /**
     * 模型
     */
    private String model;
    /**
     * 示例内容
     */
    private String example;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 情感倾向
     */
    private String sentiment;
    /**
     * 引导方向
     */
    private String guideline;
    /**
     * 关键词
     */
    private String keywords;
    /**
     * 生成数量
     */
    private Long commentCount;
    /**
     * 长度限制
     */
    private Long maxWords;
    /**
     * 生成内容
     */
    private String aiContent;
    /**
     * 操作类型
     */
    private String operationType;
    /**
     * 生成状态
     */
    private String generateStatus;

}
