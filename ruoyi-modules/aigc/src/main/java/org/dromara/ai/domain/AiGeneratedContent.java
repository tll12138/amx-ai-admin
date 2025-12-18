package org.dromara.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.io.Serial;
import java.util.Date;

/**
 * AI生成内容解析对象 ai_generated_content
 *
 * @author LL
 * @date 2025-12-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_generated_content")
public class AiGeneratedContent extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 主键ID
         */
        @TableId(value = "id", type = IdType.ASSIGN_UUID)
        private String id;
        /**
         * 关联的生成任务ID
         */
        private String taskId;
        /**
         * 原始AI生成内容
         */
        private String originalContent;
        /**
         * 解析后的标题
         */
        private String parsedTitle;
        /**
         * 解析后的段落（JSON格式）
         */
        private String parsedParagraphs;
        /**
         * 关键词分析（JSON格式）
         */
        private String keywordAnalysis;
        /**
         * 生成时间
         */
        private Date generateTime;

}
