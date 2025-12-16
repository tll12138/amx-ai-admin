package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import kotlin.jvm.internal.Lambda;
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
import org.dromara.ai.domain.bo.AiGoodsRelationsBo;
import org.dromara.ai.domain.vo.AiGoodsRelationsVo;
import org.dromara.ai.domain.AiGoodsRelations;
import org.dromara.ai.mapper.AiGoodsRelationsMapper;
import org.dromara.ai.service.IAiGoodsRelationsService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;
import static org.dromara.common.satoken.utils.LoginHelper.getUserId;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商品竞品关系Service业务层处理
 *
 * @author ZRL
 * @date 2025-03-25
 */
@RequiredArgsConstructor
@Service
public class AiGoodsRelationsServiceImpl implements IAiGoodsRelationsService {

    private final AiGoodsRelationsMapper baseMapper;


    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiGoodsRelations entity){
        //TODO 做一些数据校验,如唯一约束
    }

    @Override
    public Boolean batchBindAiGoodsRelations(String competitorId, List<String> productIds) {
        if (StringUtils.isBlank(competitorId) || CollectionUtil.isEmpty(productIds)) {
            throw new ServiceException("竞品id或商品id不能为空");
        }
        Long creator = Opt.ofNullable(getUserId()).orElse(0L);
        if (StringUtils.isNotBlank(competitorId) && !productIds.isEmpty()) {
            List<AiGoodsRelations> list = productIds.stream().map(productId -> {
                AiGoodsRelations aiGoodsRelations = new AiGoodsRelations();
                aiGoodsRelations.setProductId(productId);
                aiGoodsRelations.setCompetitorId(competitorId);
                aiGoodsRelations.setCreateBy(creator);
                return aiGoodsRelations;
            }).toList();
            return baseMapper.insertBatch(list);
        }
        return false;
    }

    @Override
    public List<String> queryRelationsByProductId(String productId) {
        if (StringUtils.isBlank(productId)) {
            throw new ServiceException("商品id不能为空");
        }
        List<String> competitorIds = baseMapper.selectObjs(
            new LambdaQueryWrapper<AiGoodsRelations>()
                .eq(AiGoodsRelations::getProductId, productId)
                .select(AiGoodsRelations::getCompetitorId),
            (obj) -> (String) obj
        );
        return CollectionUtil.isEmpty(competitorIds) ? Collections.emptyList() : competitorIds;
    }

    @Override
    public List<String> queryRelationsByCompetitorId(String competitorId) {
        if (StringUtils.isBlank(competitorId)) {
            throw new ServiceException("竞品id不能为空");
        }
        List<String> productIds = baseMapper.selectObjs(
            new LambdaQueryWrapper<AiGoodsRelations>()
                .eq(AiGoodsRelations::getCompetitorId, competitorId)
                .select(AiGoodsRelations::getProductId),
            (obj) -> (String) obj
        );
        return CollectionUtil.isEmpty(productIds) ? Collections.emptyList() : productIds;
    }

    /**
     * 校验并根据竞品ID 批量删除绑定关系
     *
     * @param competitorId 竞品ID
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByCompetitorId(String competitorId, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
            if (StringUtils.isBlank(competitorId)){
                throw new ServiceException("竞品id不能为空");
            }
        }
        LambdaQueryWrapper<AiGoodsRelations> deleteWrapper =
            new LambdaQueryWrapper<AiGoodsRelations>()
                .setEntityClass(AiGoodsRelations.class)
                .eq(AiGoodsRelations::getCompetitorId, competitorId);
        List<AiGoodsRelations> list = Db.list(deleteWrapper);
        if (CollectionUtil.isEmpty(list)){
            return true;
        }
        return baseMapper.delete(deleteWrapper) > 0;
    }

    @Override
    public Boolean batchDeleteWithValidByCompetitorIds(List<String> competitorIds, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
            if (CollectionUtil.isEmpty(competitorIds)){
                throw new ServiceException("竞品id不能为空");
            }
        }
        LambdaQueryWrapper<AiGoodsRelations> deleteWrapper =
            new LambdaQueryWrapper<AiGoodsRelations>()
                .in(AiGoodsRelations::getCompetitorId, competitorIds);
        List<AiGoodsRelations> list = baseMapper.selectList(deleteWrapper);
        if (CollectionUtil.isEmpty(list)){
            return true;
        }
        return baseMapper.delete(deleteWrapper) > 0;
    }

    @Override
    public Boolean updateAiGoodsRelations(String competitorId, List<String> productIds) {
        return
            this.deleteWithValidByCompetitorId(competitorId, true) &&
            this.batchBindAiGoodsRelations(competitorId, productIds);
    }
}
