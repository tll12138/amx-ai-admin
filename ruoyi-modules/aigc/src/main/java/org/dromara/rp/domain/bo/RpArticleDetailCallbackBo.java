package org.dromara.rp.domain.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseDo;

/**
 * 文章任务明细回调
 *
 * @author ll
 * @date 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RpArticleDetailCallbackBo extends BaseDo {

    /**
     * 任务id
     */
    private Long id;

    /**
     * 发布状态
     */
    private String status;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 发布截图
     */
    private String img;

}
