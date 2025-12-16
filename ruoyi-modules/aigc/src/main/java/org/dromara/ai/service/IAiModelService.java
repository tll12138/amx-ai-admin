package org.dromara.ai.service;

import org.dromara.ai.domain.option.BaseOptions;
import org.dromara.ai.domain.option.ModelOptions;
import org.dromara.ai.domain.vo.AiModelVo;
import org.dromara.ai.domain.bo.AiModelBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * AI模型配置Service接口
 *
 * @author ZRL
 * @date 2025-04-02
 */
public interface IAiModelService {

    /**
     * 查询AI模型配置
     *
     * @param id 主键
     * @return AI模型配置
     */
    AiModelVo queryById(String id);

    /**
     * 分页查询AI模型配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI模型配置分页列表
     */
    TableDataInfo<AiModelVo> queryPageList(AiModelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的AI模型配置列表
     *
     * @param bo 查询条件
     * @return AI模型配置列表
     */
    List<AiModelVo> queryList(AiModelBo bo);

    /**
     * 新增AI模型配置
     *
     * @param bo AI模型配置
     * @return 是否新增成功
     */
    Boolean insertAiModel(AiModelBo bo);

    /**
     * 修改AI模型配置
     *
     * @param bo AI模型配置
     * @return 是否修改成功
     */
    Boolean updateAiModel(AiModelBo bo);

    /**
     * 校验并批量删除AI模型配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 批量导入AI模型配置数据
     * @param aiModelList AI模型配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    String importAiModel(List<AiModelVo> aiModelList, boolean updateSupport, String operatorName);

    List<ModelOptions> getModelOptions();
}
