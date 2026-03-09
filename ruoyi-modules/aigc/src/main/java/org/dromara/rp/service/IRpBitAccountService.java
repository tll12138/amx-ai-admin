package org.dromara.rp.service;

import org.dromara.rp.domain.vo.RpBitAccountVo;
import org.dromara.rp.domain.bo.RpBitAccountBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 比特账号信息Service接口
 *
 * @author LL
 * @date 2026-03-02
 */
public interface IRpBitAccountService {

    /**
     * 查询比特账号信息
     *
     * @param id 主键
     * @return 比特账号信息
     */
    RpBitAccountVo queryById(Long id);

    /**
     * 分页查询比特账号信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比特账号信息分页列表
     */
    TableDataInfo<RpBitAccountVo> queryPageList(RpBitAccountBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比特账号信息列表
     *
     * @param bo 查询条件
     * @return 比特账号信息列表
     */
    List<RpBitAccountVo> queryList(RpBitAccountBo bo);

    /**
     * 新增比特账号信息
     *
     * @param bo 比特账号信息
     * @return 是否新增成功
     */
    Boolean insertRpBitAccount(RpBitAccountBo bo);

    /**
     * 修改比特账号信息
     *
     * @param bo 比特账号信息
     * @return 是否修改成功
     */
    Boolean updateRpBitAccount(RpBitAccountBo bo);

    /**
     * 校验并批量删除比特账号信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入比特账号信息数据
     * @param rpBitAccountList 比特账号信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importRpBitAccount(List<RpBitAccountVo> rpBitAccountList, boolean updateSupport, String operatorName);
}
