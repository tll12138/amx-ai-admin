package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.dromara.ai.domain.option.BaseOptions;
import org.dromara.ai.domain.option.ModelOptions;
import org.dromara.ai.enums.EnableEnum;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.ai.domain.bo.AiModelBo;
import org.dromara.ai.domain.vo.AiModelVo;
import org.dromara.ai.domain.AiModel;
import org.dromara.ai.mapper.AiModelMapper;
import org.dromara.ai.service.IAiModelService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Collection;

/**
 * AI模型配置Service业务层处理
 *
 * @author ZRL
 * @date 2025-04-02
 */
@RequiredArgsConstructor
@Service
public class AiModelServiceImpl implements IAiModelService {

    private final AiModelMapper baseMapper;

    /**
     * 查询AI模型配置
     *
     * @param id 主键
     * @return AI模型配置
     */
    @Override
    public AiModelVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI模型配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI模型配置分页列表
     */
    @Override
    public TableDataInfo<AiModelVo> queryPageList(AiModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiModel> lqw = buildQueryWrapper(bo);
        Page<AiModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI模型配置列表
     *
     * @param bo 查询条件
     * @return AI模型配置列表
     */
    @Override
    public List<AiModelVo> queryList(AiModelBo bo) {
        LambdaQueryWrapper<AiModel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiModel> buildQueryWrapper(AiModelBo bo) {
        LambdaQueryWrapper<AiModel> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getModel()), AiModel::getModel, bo.getModel());
        lqw.eq(StringUtils.isNotBlank(bo.getProvider()), AiModel::getProvider, bo.getProvider());
        lqw.eq(StringUtils.isNotBlank(bo.getEnable()), AiModel::getEnable, bo.getEnable());
        lqw.like(StringUtils.isNotBlank(bo.getName()), AiModel::getName, bo.getName());
        lqw.orderByAsc(AiModel::getOrders);
        return lqw;
    }

    /**
     * 新增AI模型配置
     *
     * @param bo AI模型配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertAiModel(AiModelBo bo) {
        AiModel add = MapstructUtils.convert(bo, AiModel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改AI模型配置
     *
     * @param bo AI模型配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiModel(AiModelBo bo) {
        AiModel update = MapstructUtils.convert(bo, AiModel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiModel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除AI模型配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量导入AI模型配置数据
     * @param aiModelList AI模型配置列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importAiModel(List<AiModelVo> aiModelList, boolean updateSupport, String operatorName){
        return ImportEntities(
            aiModelList,
            updateSupport,
            operatorName,
            this::importAiModelInfo,
            // TODO 自定义名称
            (item) -> Opt.ofNullable(item.getId()).orElseGet(()->"")
        );
    }

    /**
     * 导入AI模型配置信息
     * @param aiModelVo AI模型配置信息
     * @param operatorName 操作人
     */
    private void importAiModelInfo(AiModelVo aiModelVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        AiModelBo aiModelBo = BeanUtil.toBean(aiModelVo, AiModelBo.class);
        if (aiModelBo.getId() == null){
            ValidatorUtils.validate(aiModelBo, AddGroup.class);
            this.insertAiModel(aiModelBo);
        }else {
            ValidatorUtils.validate(aiModelBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            AiModel aiModel = Db.getById(aiModelBo.getId(), AiModel.class);
            if (aiModel == null){
                aiModelBo.setId(null);
                this.insertAiModel(aiModelBo);
                return;
            }
            this.updateAiModel(aiModelBo);
        }
    }

    @Override
    public List<ModelOptions> getModelOptions() {
        List<AiModel> aiModels = baseMapper.selectList(
            Wrappers.lambdaQuery(AiModel.class)
                .eq(AiModel::getEnable, EnableEnum.ON.getValue())
                .orderByAsc(AiModel::getOrders)
        );
        return aiModels.stream().map(aiModel -> {
            ModelOptions aiModelOptions = new ModelOptions();
            aiModelOptions.setLabel(aiModel.getName());
            aiModelOptions.setValue(aiModel.getId());
            aiModelOptions.setTemperature(aiModel.getTemperature());
            aiModelOptions.setTopP(aiModel.getTopP());
            aiModelOptions.setProvider(aiModel.getProvider());
            return aiModelOptions;
        }).toList();
    }
}
