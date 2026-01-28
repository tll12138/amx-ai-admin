package org.dromara.rp.service;

import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.domain.bo.RpaAccountConfigBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * RPA账号配置Service接口
 *
 * @author LL
 * @date 2026-01-28
 */
public interface IRpaAccountConfigService {

    /**
     * 查询RPA账号配置
     *
     * @param id 主键
     * @return RPA账号配置
     */
    RpaAccountConfigVo queryById(Long id);

    /**
     * 分页查询RPA账号配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return RPA账号配置分页列表
     */
    TableDataInfo<RpaAccountConfigVo> queryPageList(RpaAccountConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的RPA账号配置列表
     *
     * @param bo 查询条件
     * @return RPA账号配置列表
     */
    List<RpaAccountConfigVo> queryList(RpaAccountConfigBo bo);

    /**
     * 新增RPA账号配置
     *
     * @param bo RPA账号配置
     * @return 是否新增成功
     */
    Boolean insertRpaAccountConfig(RpaAccountConfigBo bo);

    /**
     * 修改RPA账号配置
     *
     * @param bo RPA账号配置
     * @return 是否修改成功
     */
    Boolean updateRpaAccountConfig(RpaAccountConfigBo bo);

    /**
     * 校验并批量删除RPA账号配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入RPA账号配置数据
     * @param rpaAccountConfigList RPA账号配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importRpaAccountConfig(List<RpaAccountConfigVo> rpaAccountConfigList, boolean updateSupport, String operatorName);

    /**
     * 定时获取RPA机器人账号存库
     */
    void getRpaRobotAccount();
}
