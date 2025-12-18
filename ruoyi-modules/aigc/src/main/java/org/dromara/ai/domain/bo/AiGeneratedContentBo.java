package org.dromara.ai.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.ai.domain.AiGeneratedContent;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseDo;

import java.util.Date;

/**
 * AI生成内容解析业务对象 ai_generated_content
 *
 * @author LL
 * @date 2025-12-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiGeneratedContent.class, reverseConvertGenerate = false)
public class AiGeneratedContentBo extends BaseDo {

        /**
         * 主键ID
         */
        @NotBlank(message = "主键ID不能为空", groups = { EditGroup.class })
        private String id;

        /**
         * 关联的生成任务ID
         */
        @NotBlank(message = "关联的生成任务ID不能为空", groups = { AddGroup.class, EditGroup.class })
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
        @NotNull(message = "生成时间不能为空", groups = { AddGroup.class, EditGroup.class })
        private Date generateTime;


}
