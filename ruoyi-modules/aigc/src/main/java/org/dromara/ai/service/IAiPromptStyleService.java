package org.dromara.ai.service;

import org.dromara.ai.domain.vo.AiPromptStyleVo;
import org.dromara.ai.domain.bo.AiPromptStyleBo;
import org.dromara.ai.domain.option.BaseOptions;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * AI内容风格Service接口
 *
 * @author ZRL
 * @date 2025-04-07
 */
public interface IAiPromptStyleService {

    /**
     * 查询AI内容风格
     *
     * @param id 主键
     * @return AI内容风格
     */
    AiPromptStyleVo queryById(String id);

    /**
     * 分页查询AI内容风格列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI内容风格分页列表
     */
    TableDataInfo<AiPromptStyleVo> queryPageList(AiPromptStyleBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI内容风格列表
     *
     * @param bo 查询条件
     * @return AI内容风格列表
     */
    List<AiPromptStyleVo> queryList(AiPromptStyleBo bo);

    /**
     * 新增AI内容风格
     *
     * @param bo AI内容风格
     * @return 是否新增成功
     */
    String insertAiPromptStyle(AiPromptStyleBo bo);

    /**
     * 修改AI内容风格
     *
     * @param bo AI内容风格
     * @return 是否修改成功
     */
    Boolean updateAiPromptStyle(AiPromptStyleBo bo);

    /**
     * 校验并批量删除AI内容风格信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 批量导入AI内容风格数据
     * @param aiPromptStyleList AI内容风格列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importAiPromptStyle(List<AiPromptStyleVo> aiPromptStyleList, boolean updateSupport, String operatorName);

    List<AiPromptStyleVo> getStyleOptions(String type);

}
