package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.dromara.ai.domain.AiModel;
import org.dromara.ai.domain.option.BaseOptions;
import org.dromara.ai.enums.EnableEnum;
import org.dromara.common.core.exception.ServiceException;
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
import org.dromara.ai.domain.bo.AiPromptStyleBo;
import org.dromara.ai.domain.vo.AiPromptStyleVo;
import org.dromara.ai.domain.AiPromptStyle;
import org.dromara.ai.mapper.AiPromptStyleMapper;
import org.dromara.ai.service.IAiPromptStyleService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * AI内容风格Service业务层处理
 *
 * @author ZRL
 * @date 2025-04-07
 */
@RequiredArgsConstructor
@Service
public class AiPromptStyleServiceImpl implements IAiPromptStyleService {

    private final AiPromptStyleMapper baseMapper;

    /**
     * 查询AI内容风格
     *
     * @param id 主键
     * @return AI内容风格
     */
    @Override
    public AiPromptStyleVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI内容风格列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI内容风格分页列表
     */
    @Override
    public TableDataInfo<AiPromptStyleVo> queryPageList(AiPromptStyleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiPromptStyle> lqw = buildQueryWrapper(bo);
        Page<AiPromptStyleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI内容风格列表
     *
     * @param bo 查询条件
     * @return AI内容风格列表
     */
    @Override
    public List<AiPromptStyleVo> queryList(AiPromptStyleBo bo) {
        LambdaQueryWrapper<AiPromptStyle> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiPromptStyle> buildQueryWrapper(AiPromptStyleBo bo) {
        LambdaQueryWrapper<AiPromptStyle> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getName()), AiPromptStyle::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), AiPromptStyle::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getEnable()), AiPromptStyle::getEnable, bo.getEnable());
        lqw.eq(StringUtils.isNotBlank(bo.getPrompt()), AiPromptStyle::getPrompt, bo.getPrompt());
        lqw.orderByAsc(AiPromptStyle::getOrders);
        return lqw;
    }

    /**
     * 新增AI内容风格
     *
     * @param bo AI内容风格
     * @return 是否新增成功
     */
    @Override
    public String insertAiPromptStyle(AiPromptStyleBo bo) {
        AiPromptStyle add = MapstructUtils.convert(bo, AiPromptStyle.class);
        validEntityBeforeSave(add, true);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return add.getId();
    }

    /**
     * 修改AI内容风格
     *
     * @param bo AI内容风格
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiPromptStyle(AiPromptStyleBo bo) {
        AiPromptStyle update = MapstructUtils.convert(bo, AiPromptStyle.class);
        validEntityBeforeSave(update, false);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiPromptStyle entity, boolean isAdd){
        //TODO 做一些数据校验,如唯一约束
        AiPromptStyle one = Db.getOne(
            Wrappers.lambdaQuery(AiPromptStyle.class)
                .eq(AiPromptStyle::getName, entity.getName())
                .eq(AiPromptStyle::getType, entity.getType())
        );
        if(one != null && isAdd){
            throw new ServiceException("此名称同类型的提示词已存在");
        }else if (one != null && !entity.getId().equals(one.getId())){
            throw new ServiceException("此名称同类型的提示词已存在");
        }
    }

    /**
     * 校验并批量删除AI内容风格信息
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
     * 批量导入AI内容风格数据
     * @param aiPromptStyleList AI内容风格列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importAiPromptStyle(List<AiPromptStyleVo> aiPromptStyleList, boolean updateSupport, String operatorName){
        return ImportEntities(
            aiPromptStyleList,
            updateSupport,
            operatorName,
            this::importAiPromptStyleInfo,
            // TODO 自定义名称
            (item) -> Opt.ofNullable(item.getId()).orElseGet(()->"")
        );
    }

    /**
     * 导入AI内容风格信息
     * @param aiPromptStyleVo AI内容风格信息
     * @param operatorName 操作人
     */
    private void importAiPromptStyleInfo(AiPromptStyleVo aiPromptStyleVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        AiPromptStyleBo aiPromptStyleBo = BeanUtil.toBean(aiPromptStyleVo, AiPromptStyleBo.class);
        if (aiPromptStyleBo.getId() == null){
            ValidatorUtils.validate(aiPromptStyleBo, AddGroup.class);
            this.insertAiPromptStyle(aiPromptStyleBo);
        }else {
            ValidatorUtils.validate(aiPromptStyleBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            AiPromptStyle aiPromptStyle = Db.getById(aiPromptStyleBo.getId(), AiPromptStyle.class);
            if (aiPromptStyle == null){
                aiPromptStyleBo.setId(null);
                this.insertAiPromptStyle(aiPromptStyleBo);
                return;
            }
            this.updateAiPromptStyle(aiPromptStyleBo);
        }
    }

    @Override
    public List<AiPromptStyleVo> getStyleOptions(String type) {
        List<AiPromptStyle> aiModels = baseMapper.selectList(
            Wrappers.lambdaQuery(AiPromptStyle.class)
                .eq(AiPromptStyle::getEnable, EnableEnum.ON.getValue())
                .like(AiPromptStyle::getType, type)
                .orderByAsc(AiPromptStyle::getOrders)
        );
        return MapstructUtils.convert(aiModels, AiPromptStyleVo.class);
    }
}
