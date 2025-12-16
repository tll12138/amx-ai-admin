package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Opt;
import com.baomidou.mybatisplus.extension.toolkit.Db;
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
import org.dromara.ai.domain.bo.AiProductKeywordsBo;
import org.dromara.ai.domain.vo.AiProductKeywordsVo;
import org.dromara.ai.domain.AiProductKeywords;
import org.dromara.ai.mapper.AiProductKeywordsMapper;
import org.dromara.ai.service.IAiProductKeywordsService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 产品-关键词关系Service业务层处理
 *
 * @author ZRL
 * @date 2025-03-26
 */
@RequiredArgsConstructor
@Service
public class AiProductKeywordsServiceImpl implements IAiProductKeywordsService {

    private final AiProductKeywordsMapper baseMapper;

    /**
     * 查询产品-关键词关系
     *
     * @param productId 主键
     * @return 产品-关键词关系
     */
    @Override
    public List<AiProductKeywords> queryByProductId(String productId){
        List<AiProductKeywords> list = Db.lambdaQuery(AiProductKeywords.class)
            .select(AiProductKeywords::getKeyword, AiProductKeywords::getWeight)
            .eq(AiProductKeywords::getProductId, productId)
            .orderByDesc(AiProductKeywords::getWeight)
            .list();
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }

    /**
     * 新增产品-关键词关系
     *
     * @param bo 产品-关键词关系
     * @return 是否新增成功
     */
    @Override
    public Boolean insertAiProductKeywords(AiProductKeywordsBo bo) {
        AiProductKeywords add = MapstructUtils.convert(bo, AiProductKeywords.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
        }
        return flag;
    }

    /**
     * 修改产品-关键词关系
     *
     * @param bo 产品-关键词关系
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiProductKeywords(AiProductKeywordsBo bo) {
        AiProductKeywords update = MapstructUtils.convert(bo, AiProductKeywords.class);
        validEntityBeforeSave(update);
        return baseMapper.insertOrUpdate(update);
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiProductKeywords entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除产品-关键词关系信息
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
     * 删除某个产品所有的关键词关系信息
     *
     * @param productId  待删除的主键
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteByProductId(String productId) {
        LambdaQueryWrapper<AiProductKeywords> wrapper = Wrappers.lambdaQuery(AiProductKeywords.class)
            .eq(AiProductKeywords::getProductId, productId);
        List<AiProductKeywords> list = Db.list(wrapper);
        if (CollectionUtil.isEmpty(list)){
            return true;
        }
        return baseMapper.delete(wrapper) > 0;
    }

    /**
     * 删除某个产品特定的关键词关系信息
     *
     * @param bo  特定的关键词关系信息
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteKeyword(AiProductKeywordsBo bo) {
        LambdaQueryWrapper<AiProductKeywords> wrapper = Wrappers.lambdaQuery(AiProductKeywords.class)
            .eq(AiProductKeywords::getProductId, bo.getProductId())
            .eq(AiProductKeywords::getKeyword, bo.getKeyword());
        return baseMapper.delete(wrapper) > 0;
    }
}
