package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.dromara.ai.domain.bo.AiProductKeywordsBo;
import org.dromara.ai.domain.vo.AiProductKeywordsVo;
import org.dromara.ai.enums.ProductType;
import org.dromara.ai.service.IAiGoodsRelationsService;
import org.dromara.ai.service.IAiProductKeywordsService;
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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.dromara.ai.domain.bo.AiGoodsBo;
import org.dromara.ai.domain.vo.AiGoodsVo;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.mapper.AiGoodsMapper;
import org.dromara.ai.service.IAiGoodsService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 产品信息Service业务层处理
 *
 * @author ZRL
 * @date 2025-03-24
 */
@RequiredArgsConstructor
@Service
public class AiGoodsServiceImpl implements IAiGoodsService {

    private final AiGoodsMapper baseMapper;

    private final IAiGoodsRelationsService goodsRelationsService;

    private final IAiProductKeywordsService aiProductKeywordsService;

    /**
     * 查询产品信息
     *
     * @param id 主键
     * @return 产品信息
     */
    @Override
    public AiGoodsVo queryById(String id){
        AiGoodsVo aiGoodsVo = baseMapper.selectVoById(id);
        if (aiGoodsVo == null){
            throw new ServiceException("产品信息不存在");
        }
        if (ProductType.COMPETITOR.getType().equals(aiGoodsVo.getType())){
            aiGoodsVo.setProductIds(goodsRelationsService.queryRelationsByCompetitorId(id));
        }else{
            aiGoodsVo.setProductIds(goodsRelationsService.queryRelationsByProductId(id));
        }
        return aiGoodsVo;
    }

    /**
     * 分页查询产品信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品信息分页列表
     */
    @Override
    public TableDataInfo<AiGoodsVo> queryPageList(AiGoodsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiGoods> lqw = buildQueryWrapper(bo);
        Page<AiGoodsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的产品信息列表
     *
     * @param bo 查询条件
     * @return 产品信息列表
     */
    @Override
    public List<AiGoodsVo> queryList(AiGoodsBo bo) {
        LambdaQueryWrapper<AiGoods> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiGoods> buildQueryWrapper(AiGoodsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiGoods> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(AiGoods::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), AiGoods::getName, bo.getName());
        lqw.like(StringUtils.isNotBlank(bo.getBrand()), AiGoods::getBrand, bo.getBrand());
        lqw.like(StringUtils.isNotBlank(bo.getEffect()), AiGoods::getEffect, bo.getEffect());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), AiGoods::getType, bo.getType());
        return lqw;
    }

    /**
     * 新增产品信息
     *
     * @param bo 产品信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertAiGoods(AiGoodsBo bo) {

        AiGoods add = MapstructUtils.convert(bo, AiGoods.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            List<String> productIds = bo.getProductIds();
            if (CollectionUtil.isEmpty(productIds)){
                return true;
            }
            return goodsRelationsService.batchBindAiGoodsRelations(add.getId(), productIds);
        }
        return false;
    }

    /**
     * 修改产品信息
     *
     * @param bo 产品信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiGoods(AiGoodsBo bo) {
        AiGoods update = MapstructUtils.convert(bo, AiGoods.class);
        validEntityBeforeSave(update);
        boolean flag = baseMapper.updateById(update) > 0;
        if (flag) {
            List<String> productIds = bo.getProductIds();
            if (CollectionUtil.isEmpty(productIds)){
                return goodsRelationsService.deleteWithValidByCompetitorId(bo.getId(), true);
            }
            return goodsRelationsService.updateAiGoodsRelations(bo.getId(), productIds);
        }
        return false;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiGoods entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品信息信息
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
        return goodsRelationsService.batchDeleteWithValidByCompetitorIds((List<String>) ids,true)
                && baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量导入产品信息数据
     * @param aiGoodsList 产品信息列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importAiGoods(List<AiGoodsVo> aiGoodsList, boolean updateSupport, String operatorName){
        return ImportEntities(
            aiGoodsList,
            updateSupport,
            operatorName,
            this::importAiGoodsInfo,
            // TODO 自定义名称
            item -> Opt.ofNullable(item.getName()).orElseGet(()->"")
        );
    }

    @Override
    public String importAiGoodsKeywords(List<AiProductKeywordsVo> dataList, boolean updateSupport, String operatorName) {
        return ImportEntities(
            dataList,
            updateSupport,
            operatorName,
            this::importAiGoodsKeywords,
            // TODO 自定义名称
            item -> Opt.ofNullable(item.getProductName()).orElseGet(()->"")
        );
    }

    /**
     * 导入产品信息信息
     * @param aiGoodsVo 产品信息信息
     * @param operatorName 操作人
     */
    private void importAiGoodsInfo(AiGoodsVo aiGoodsVo, String operatorName) {
        // TODO 自定义导入逻辑
        AiGoodsBo aiGoodsBo = BeanUtil.toBean(aiGoodsVo, AiGoodsBo.class);
        if (aiGoodsVo.getType() == null){
            // 默认设置为竞品
            aiGoodsBo.setType(ProductType.COMPETITOR.getType());
        }
        String effect = aiGoodsVo.getEffect();
        if (effect != null){
            // 换行回车替换为空格
            aiGoodsBo.setEffect(effect.trim().replaceAll("[\\r\\n]+", " "));
        }
        if (aiGoodsBo.getId() == null){
            ValidatorUtils.validate(aiGoodsBo, AddGroup.class);
            this.insertAiGoods(aiGoodsBo);
        }else {
            ValidatorUtils.validate(aiGoodsBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            AiGoods aiGoods = Db.getById(aiGoodsBo.getId(), AiGoods.class);
            if (aiGoods == null){
                aiGoodsBo.setId(null);
                this.insertAiGoods(aiGoodsBo);
                return;
            }
            this.updateAiGoods(aiGoodsBo);
        }
    }

    /**
     * 导入产品关键词信息
     * @param aiProductKeywordsVo 产品信息信息
     * @param operatorName 操作人
     */
    private void importAiGoodsKeywords(AiProductKeywordsVo aiProductKeywordsVo, String operatorName) {
        // TODO 自定义导入逻辑
        AiProductKeywordsBo aiProductKeywordsBo = BeanUtil.toBean(aiProductKeywordsVo, AiProductKeywordsBo.class);
        ValidatorUtils.validate(aiProductKeywordsBo, AddGroup.class, EditGroup.class);
        aiProductKeywordsService.updateAiProductKeywords(aiProductKeywordsBo);
    }

    @Override
    public JSONArray getSelfGoods() {
        List<AiGoodsVo> aiGoodsVoList = baseMapper.selectVoList(
            new LambdaQueryWrapper<AiGoods>().eq(AiGoods::getType, ProductType.PRODUCT.getType())
        );
        return handleDataToOptions(aiGoodsVoList);
    }

    @Override
    public JSONArray getCompetitorOptions(String id) {

        List<String> competitorIdList = goodsRelationsService.queryRelationsByProductId(id);
        if (CollectionUtil.isEmpty(competitorIdList)){
            return new JSONArray();
        }

        List<AiGoodsVo> aiGoodsVoList = baseMapper.selectVoList(
            new LambdaQueryWrapper<AiGoods>().in(AiGoods::getId, competitorIdList)
        );
        JSONArray jsonArray = new JSONArray();
        for (AiGoodsVo aiGoodsVo : aiGoodsVoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("label", aiGoodsVo.getName());
            jsonObject.set("value", aiGoodsVo.getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    @NotNull
    private static JSONArray handleDataToOptions(List<AiGoodsVo> aiGoodsVoList) {
        Map<String, List<AiGoodsVo>> collect =
            aiGoodsVoList.stream().collect(Collectors.groupingBy(AiGoodsVo::getBrand));
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, List<AiGoodsVo>> entry : collect.entrySet()) {
            JSONObject entries = new JSONObject();
            entries.set("label", entry.getKey());
            List<JSONObject> options = entry.getValue().stream().map(item -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.set("label", item.getName());
                jsonObject.set("value", item.getId());
                return jsonObject;
            }).toList();
            entries.set("options", options);
            jsonArray.add(entries);
        }
        return jsonArray;
    }
}
