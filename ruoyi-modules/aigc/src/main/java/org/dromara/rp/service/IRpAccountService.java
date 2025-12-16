package org.dromara.rp.service;

import org.dromara.rp.domain.vo.RpAccountVo;
import org.dromara.rp.domain.bo.RpAccountBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 账号信息Service接口
 *
 * @author ZRL
 * @date 2025-11-13
 */
public interface IRpAccountService {

    /**
     * 查询账号信息
     *
     * @param id 主键
     * @return 账号信息
     */
    RpAccountVo queryById(Long id);

    /**
     * 分页查询账号信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 账号信息分页列表
     */
    TableDataInfo<RpAccountVo> queryPageList(RpAccountBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的账号信息列表
     *
     * @param bo 查询条件
     * @return 账号信息列表
     */
    List<RpAccountVo> queryList(RpAccountBo bo);

    /**
     * 新增账号信息
     *
     * @param bo 账号信息
     * @return 是否新增成功
     */
    Boolean insertRpAccount(RpAccountBo bo);

    /**
     * 修改账号信息
     *
     * @param bo 账号信息
     * @return 是否修改成功
     */
    Boolean updateRpAccount(RpAccountBo bo);

    /**
     * 校验并批量删除账号信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入账号信息数据
     * @param rpAccountList 账号信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importRpAccount(List<RpAccountVo> rpAccountList, boolean updateSupport, String operatorName);
}
