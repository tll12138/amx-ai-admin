package org.dromara.rp.service;

import org.dromara.rp.domain.vo.YdAppConfigVo;
import org.dromara.rp.domain.bo.YdAppConfigBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 影刀应用配置Service接口
 *
 * @author LL
 * @date 2026-03-12
 */
public interface IYdAppConfigService {

    /**
     * 查询影刀应用配置
     *
     * @param id 主键
     * @return 影刀应用配置
     */
    YdAppConfigVo queryById(Long id);

    /**
     * 分页查询影刀应用配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 影刀应用配置分页列表
     */
    TableDataInfo<YdAppConfigVo> queryPageList(YdAppConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的影刀应用配置列表
     *
     * @param bo 查询条件
     * @return 影刀应用配置列表
     */
    List<YdAppConfigVo> queryList(YdAppConfigBo bo);

    /**
     * 新增影刀应用配置
     *
     * @param bo 影刀应用配置
     * @return 是否新增成功
     */
    Boolean insertYdAppConfig(YdAppConfigBo bo);

    /**
     * 修改影刀应用配置
     *
     * @param bo 影刀应用配置
     * @return 是否修改成功
     */
    Boolean updateYdAppConfig(YdAppConfigBo bo);

    /**
     * 校验并批量删除影刀应用配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入影刀应用配置数据
     * @param ydAppConfigList 影刀应用配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importYdAppConfig(List<YdAppConfigVo> ydAppConfigList, boolean updateSupport, String operatorName);
}
