package org.dromara.rp.service;

import org.dromara.rp.domain.vo.RpArticleDetailVo;
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 文章任务明细Service接口
 *
 * @author ZRL
 * @date 2025-11-13
 */
public interface IRpArticleDetailService {

    /**
     * 查询文章任务明细
     *
     * @param id 主键
     * @return 文章任务明细
     */
    RpArticleDetailVo queryById(Long id);

    /**
     * 分页查询文章任务明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文章任务明细分页列表
     */
    TableDataInfo<RpArticleDetailVo> queryPageList(RpArticleDetailBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的文章任务明细列表
     *
     * @param bo 查询条件
     * @return 文章任务明细列表
     */
    List<RpArticleDetailVo> queryList(RpArticleDetailBo bo);

    /**
     * 新增文章任务明细
     *
     * @param bo 文章任务明细
     * @return 是否新增成功
     */
    Boolean insertRpArticleDetail(RpArticleDetailBo bo);

    /**
     * 修改文章任务明细
     *
     * @param bo 文章任务明细
     * @return 是否修改成功
     */
    Boolean updateRpArticleDetail(RpArticleDetailBo bo);

    /**
     * 校验并批量删除文章任务明细信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入文章任务明细数据
     * @param rpArticleDetailList 文章任务明细列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importRpArticleDetail(List<RpArticleDetailVo> rpArticleDetailList, boolean updateSupport, String operatorName);
}
