package org.dromara.ai.domain;

import io.github.linpeilie.annotations.AutoMapping;
import org.dromara.common.mybatis.core.domain.BaseDo;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 关键词对象 ai_keywords
 *
 * @author ZRL
 * @date 2025-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ai_keywords")
public class AiKeywords extends BaseDo {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * ID
         */
        @TableId(value = "id", type = IdType.ASSIGN_UUID)
        private String id;
        /**
         * 商品ID
         */
        private String productId;

        /**
         * 类别
         */
        private String type;
        /**
         * 关键词
         */
        private String keyword;
        /**
         * 搜索指数
         */
        private Integer weight;

}
