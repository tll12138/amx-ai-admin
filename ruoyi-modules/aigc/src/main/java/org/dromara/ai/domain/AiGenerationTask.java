package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.ai.domain.vo.AiGenerationTaskVo;
import org.dromara.ai.enums.TaskTypeEnum;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;

/**
 * AI生成任务对象 ai_generation_task
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_generation_task")
@AutoMapper(target = AiGenerationTaskVo.class, uses = ConvertStrAndList.class)
public class AiGenerationTask extends BaseDo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 任务类型
     */
    private TaskTypeEnum type;

    /**
     * 创作风格
     */
    private String style;
    /**
     * 模型
     */
    private String model;
    /**
     * 笔记ID
     */
    private String noteId;
    /**
     * 笔记token
     */
    private String xsecToken;

    /**
     * 笔记标题
     */
    private String noteTitle;
    /**
     * 笔记封面
     */
    private String noteCover;
    /**
     * 笔记内容
     */
    private String noteContent;
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 竞品ID列表
     */
    private String competitorIds;

    /**
     * 埋词内容
     */
    @AutoMapping(target = "keywords", qualifiedByName = "Str2List")
    private String keywords;

    /**
     * 参考纬度
     */
    private String referenceLatitude;
    /**
     * 字数限制
     */
    private String otherLimit;
    /**
     * AI内容
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
    /**
     * 生成质量评分
     */
    private Long grade;
}
