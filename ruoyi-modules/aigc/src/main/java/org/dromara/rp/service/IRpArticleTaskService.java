package org.dromara.rp.service;

import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.bo.RpArticleTaskBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 文章任务主Service接口
 *
 * @author ZRL
 * @date 2025-11-13
 */
public interface IRpArticleTaskService {

    /**
     * 查询文章任务主
     *
     * @param id 主键
     * @return 文章任务主
     */
    RpArticleTaskVo queryById(Long id);

    /**
     * 分页查询文章任务主列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文章任务主分页列表
     */
    TableDataInfo<RpArticleTaskVo> queryPageList(RpArticleTaskBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的文章任务主列表
     *
     * @param bo 查询条件
     * @return 文章任务主列表
     */
    List<RpArticleTaskVo> queryList(RpArticleTaskBo bo);

    /**
     * 新增文章任务主
     *
     * @param bo 文章任务主
     * @return 是否新增成功
     */
    Boolean insertRpArticleTask(RpArticleTaskBo bo);

    /**
     * 修改文章任务主
     *
     * @param bo 文章任务主
     * @return 是否修改成功
     */
    Boolean updateRpArticleTask(RpArticleTaskBo bo);

    /**
     * 校验并批量删除文章任务主信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 批量导入文章任务主数据
     * @param rpArticleTaskList 文章任务主列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importRpArticleTask(List<RpArticleTaskVo> rpArticleTaskList, boolean updateSupport, String operatorName);
}
