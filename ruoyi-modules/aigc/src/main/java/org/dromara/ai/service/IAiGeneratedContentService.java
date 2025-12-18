package org.dromara.ai.service;

import org.dromara.ai.domain.AiGenerationTask;
import org.dromara.ai.domain.bo.AiGeneratedContentBo;
import org.dromara.ai.domain.vo.AiGeneratedContentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * AI生成内容解析Service接口
 *
 * @author LL
 * @date 2025-12-18
 */
public interface IAiGeneratedContentService {

    /**
     * 查询AI生成内容解析
     *
     * @param id 主键
     * @return AI生成内容解析
     */
    AiGeneratedContentVo queryById(String id);

    /**
     * 分页查询AI生成内容解析列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI生成内容解析分页列表
     */
    TableDataInfo<AiGeneratedContentVo> queryPageList(AiGeneratedContentBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI生成内容解析列表
     *
     * @param bo 查询条件
     * @return AI生成内容解析列表
     */
    List<AiGeneratedContentVo> queryList(AiGeneratedContentBo bo);

    /**
     * 新增AI生成内容解析
     *
     * @param bo AI生成内容解析
     * @return 是否新增成功
     */
    Boolean insertAiGeneratedContent(AiGeneratedContentBo bo);

    /**
     * 修改AI生成内容解析
     *
     * @param bo AI生成内容解析
     * @return 是否修改成功
     */
    Boolean updateAiGeneratedContent(AiGeneratedContentBo bo);

    /**
     * 校验并批量删除AI生成内容解析信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 批量导入AI生成内容解析数据
     * @param aiGeneratedContentList AI生成内容解析列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importAiGeneratedContent(List<AiGeneratedContentVo> aiGeneratedContentList, boolean updateSupport, String operatorName);

    /**
     * 解析并保存生成内容
     * @param task 生成任务实体（包含AI生成的内容）
     */
    void parseAndSaveContent(AiGenerationTask task);
}
