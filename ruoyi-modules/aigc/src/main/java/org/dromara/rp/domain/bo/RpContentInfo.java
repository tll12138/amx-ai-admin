package org.dromara.rp.domain.bo;

import lombok.Data;

import java.util.List;

/**
 * @author tll
 * @date 2025-12-22 16:45:44
 */
@Data
public class RpContentInfo {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 内容分组列表（每条发布内容）
     */
    private List<RpContentGroupInfo> contentGroups;
}
