package org.dromara.common.tenant.service;

import java.util.List;
import java.util.Map;

/**
 * 租户服务接口
 * 提供租户相关的基础服务方法
 * 
 * @author system
 */
public interface ITenantService {

    /**
     * 根据租户ID获取租户名称
     * 
     * @param tenantId 租户ID
     * @return 租户名称，如果不存在返回null
     */
    String getTenantNameById(String tenantId);

    /**
     * 批量根据租户ID获取租户名称
     * 
     * @param tenantIds 租户ID列表
     * @return 租户ID到租户名称的映射Map
     */
    Map<String, String> getTenantNamesByIds(List<String> tenantIds);

    /**
     * 检查租户是否存在
     * 
     * @param tenantId 租户ID
     * @return 是否存在
     */
    boolean existsTenant(String tenantId);
}