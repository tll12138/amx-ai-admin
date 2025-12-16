package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.option.BaseGroupOption;
import org.dromara.ai.enums.ProductType;
import org.dromara.ai.utils.OptionConverter;
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
import org.dromara.ai.domain.bo.AiKeywordsBo;
import org.dromara.ai.domain.vo.AiKeywordsVo;
import org.dromara.ai.domain.AiKeywords;
import org.dromara.ai.mapper.AiKeywordsMapper;
import org.dromara.ai.service.IAiKeywordsService;

import javax.swing.text.html.Option;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 关键词Service业务层处理
 *
 * @author ZRL
 * @date 2025-04-24
 */
@RequiredArgsConstructor
@Service
public class AiKeywordsServiceImpl implements IAiKeywordsService {

    private final AiKeywordsMapper baseMapper;

    /**
     * 查询关键词
     *
     * @param id 主键
     * @return 关键词
     */
    @Override
    public AiKeywordsVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询关键词列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 关键词分页列表
     */
    @Override
    public TableDataInfo<AiKeywordsVo> queryPageList(AiKeywordsBo bo, PageQuery pageQuery) {
        MPJLambdaWrapper<AiKeywords> lqw = buildQueryWrapper(bo);
        Page<AiKeywordsVo> aiKeywordsVoPage = baseMapper.selectJoinPage(pageQuery.build(), AiKeywordsVo.class, lqw);
        return TableDataInfo.build(aiKeywordsVoPage);
    }

    /**
     * 查询符合条件的关键词列表
     *
     * @param bo 查询条件
     * @return 关键词列表
     */
    @Override
    public List<AiKeywordsVo> queryList(AiKeywordsBo bo) {
        MPJLambdaWrapper<AiKeywords> lqw = buildQueryWrapper(bo);
        return baseMapper.selectJoinList(AiKeywordsVo.class, lqw);
    }

    private MPJLambdaWrapper<AiKeywords> buildQueryWrapper(AiKeywordsBo bo) {
        return new MPJLambdaWrapper<AiKeywords>()
            .selectAll(AiKeywords.class)
            .selectAs(AiGoods::getName, AiKeywordsVo::getProductName)
            .leftJoin(AiGoods.class, AiGoods::getId, AiKeywords::getProductId)
            .likeIfExists(AiKeywords::getProductId, bo.getProductId())
            .likeIfExists(AiKeywords::getType, bo.getType())
            .likeIfExists(AiKeywords::getKeyword, bo.getKeyword())
            .orderByDesc(AiKeywords::getWeight);
    }

    /**
     * 新增关键词
     *
     * @param bo 关键词
     * @return 是否新增成功
     */
    @Override
    public Boolean insertAiKeywords(AiKeywordsBo bo) {
        AiKeywords add = MapstructUtils.convert(bo, AiKeywords.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改关键词
     *
     * @param bo 关键词
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiKeywords(AiKeywordsBo bo) {
        AiKeywords update = MapstructUtils.convert(bo, AiKeywords.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiKeywords entity){
        //TODO 做一些数据校验,如唯一约束
        AiKeywords one = Db.getOne(
            Wrappers.lambdaQuery(AiKeywords.class)
                .eq(AiKeywords::getProductId, entity.getProductId())
                .eq(AiKeywords::getType, entity.getType())
                .eq(AiKeywords::getKeyword, entity.getKeyword())
        );
        if (one != null){
            throw new ServiceException("关键词已存在");
        }
    }

    /**
     * 校验并批量删除关键词信息
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
     * 批量导入关键词数据
     * @param aiKeywordsList 关键词列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importAiKeywords(List<AiKeywordsVo> aiKeywordsList, boolean updateSupport, String operatorName){
        return ImportEntities(
            aiKeywordsList,
            updateSupport,
            operatorName,
            this::importAiKeywordsInfo,
            // TODO 自定义名称
            (item) -> Opt.ofNullable(
                StrUtil.join(":",item.getProductName(),item.getType(),item.getKeyword())
            ).orElseGet(()->"")
        );
    }

    /**
     * 导入关键词信息
     * @param aiKeywordsVo 关键词信息
     * @param operatorName 操作人
     */
    private void importAiKeywordsInfo(AiKeywordsVo aiKeywordsVo, String operatorName) {
        // TODO 自定义导入逻辑
        AiKeywordsBo aiKeywordsBo = BeanUtil.toBean(aiKeywordsVo, AiKeywordsBo.class);

        // 前置逻辑获取商品ID
        if (aiKeywordsVo.getProductName() == null){
            // 默认设置为竞品
            throw new ServiceException("商品名称不能为空");
        }
        LambdaQueryWrapper<AiGoods> eq = Wrappers.lambdaQuery(AiGoods.class).eq(AiGoods::getName, aiKeywordsVo.getProductName());
        AiGoods goods = Db.getOne(eq);
        if (goods == null){
            throw new ServiceException(aiKeywordsVo.getProductName() + "商品不存在");
        }
        aiKeywordsBo.setProductId(goods.getId());

        if (aiKeywordsBo.getId() == null){
            ValidatorUtils.validate(aiKeywordsBo, AddGroup.class);

            this.insertAiKeywords(aiKeywordsBo);
        }else {
            ValidatorUtils.validate(aiKeywordsBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            AiKeywords aiKeywords = Db.getById(aiKeywordsBo.getId(), AiKeywords.class);
            if (aiKeywords == null){
                aiKeywordsBo.setId(null);
                this.insertAiKeywords(aiKeywordsBo);
                return;
            }
            this.updateAiKeywords(aiKeywordsBo);
        }
    }


    @Override
    public List<BaseGroupOption> getProductKeywordsOptions(String productId) {
        AiKeywordsBo aiKeywordsBo = new AiKeywordsBo();
        aiKeywordsBo.setProductId(productId);
        List<AiKeywordsVo> aiKeywordsVos = baseMapper.selectJoinList(AiKeywordsVo.class, buildQueryWrapper(aiKeywordsBo));
        return OptionConverter.convertToGroupOptions(
                aiKeywordsVos,
                AiKeywordsVo::getType,
                AiKeywordsVo::getKeyword,
                AiKeywordsVo::getId,
                AiKeywordsVo::getWeight
            );
    }
}
