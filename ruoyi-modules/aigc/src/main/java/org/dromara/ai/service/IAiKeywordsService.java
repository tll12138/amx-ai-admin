package org.dromara.ai.service;

import org.dromara.ai.domain.option.BaseGroupOption;
import org.dromara.ai.domain.vo.AiKeywordsVo;
import org.dromara.ai.domain.bo.AiKeywordsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 关键词Service接口
 *
 * @author ZRL
 * @date 2025-04-24
 */
public interface IAiKeywordsService {

    /**
     * 查询关键词
     *
     * @param id 主键
     * @return 关键词
     */
    AiKeywordsVo queryById(String id);

    /**
     * 分页查询关键词列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 关键词分页列表
     */
    TableDataInfo<AiKeywordsVo> queryPageList(AiKeywordsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的关键词列表
     *
     * @param bo 查询条件
     * @return 关键词列表
     */
    List<AiKeywordsVo> queryList(AiKeywordsBo bo);

    /**
     * 新增关键词
     *
     * @param bo 关键词
     * @return 是否新增成功
     */
    Boolean insertAiKeywords(AiKeywordsBo bo);

    /**
     * 修改关键词
     *
     * @param bo 关键词
     * @return 是否修改成功
     */
    Boolean updateAiKeywords(AiKeywordsBo bo);

    /**
     * 校验并批量删除关键词信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 批量导入关键词数据
     * @param aiKeywordsList 关键词列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importAiKeywords(List<AiKeywordsVo> aiKeywordsList, boolean updateSupport, String operatorName);

    List<BaseGroupOption> getProductKeywordsOptions(String productId);
}
