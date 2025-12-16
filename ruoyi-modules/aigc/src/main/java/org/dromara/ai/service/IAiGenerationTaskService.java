package org.dromara.ai.service;

import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.domain.bo.AiCommentTaskBo;
import org.dromara.ai.domain.vo.AiGenerationTaskVo;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.domain.vo.AiTaskHistoryVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * AI生成任务Service接口
 *
 * @author ZRL
 * @date 2025-03-26
 */
public interface IAiGenerationTaskService {

    /**
     * 查询AI生成任务
     *
     * @param id 主键
     * @return AI生成任务
     */
    AiGenerationTaskVo queryById(String id);

    /**
     * 分页查询AI生成任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI生成任务分页列表
     */
    TableDataInfo<AiGenerationTaskVo> queryPageList(AiGenerationTaskBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI生成任务列表
     *
     * @param bo 查询条件
     * @return AI生成任务列表
     */
    List<AiGenerationTaskVo> queryList(AiGenerationTaskBo bo);

    /**
     * 新增AI生成任务
     *
     * @param bo AI生成任务
     * @return 是否新增成功
     */
    String insertAiGenerationTask(AiGenerationTaskBo bo);

    /**
     * 修改AI生成任务
     *
     * @param bo AI生成任务
     * @return 是否修改成功
     */
    Boolean updateAiGenerationTask(AiGenerationTaskBo bo);

    /**
     * 校验并批量删除AI生成任务信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    List<AiTaskHistoryVo> queryAll();

    List<String> getCompetitors(String id);

}
