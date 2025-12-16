package org.dromara.rp.service;

import org.dromara.common.core.utils.dropdown.common.DropDownOptionsGroup;
import org.dromara.rp.domain.vo.RpAccountGroupVo;
import org.dromara.rp.domain.bo.RpAccountGroupBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 账号分组Service接口
 *
 * @author ZRL
 * @date 2025-11-13
 */
public interface IRpAccountGroupService {

    /**
     * 查询账号分组
     *
     * @param id 主键
     * @return 账号分组
     */
    RpAccountGroupVo queryById(Long id);

    /**
     * 分页查询账号分组列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 账号分组分页列表
     */
    TableDataInfo<RpAccountGroupVo> queryPageList(RpAccountGroupBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的账号分组列表
     *
     * @param bo 查询条件
     * @return 账号分组列表
     */
    List<RpAccountGroupVo> queryList(RpAccountGroupBo bo);

    /**
     * 新增账号分组
     *
     * @param bo 账号分组
     * @return 是否新增成功
     */
    Boolean insertRpAccountGroup(RpAccountGroupBo bo);

    /**
     * 修改账号分组
     *
     * @param bo 账号分组
     * @return 是否修改成功
     */
    Boolean updateRpAccountGroup(RpAccountGroupBo bo);

    /**
     * 校验并批量删除账号分组信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 获取账号分组下拉列表
     * @return 账号分组下拉列表
     */
    List<DropDownOptionsGroup<Long>> getSelectOptions();

    List<org.dromara.rp.domain.vo.RpAccountGroupWithAccountsVo> listGroupsWithAccountsByPlatform(String platform);
}
