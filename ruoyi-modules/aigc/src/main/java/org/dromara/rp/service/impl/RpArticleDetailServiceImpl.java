package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.rp.domain.vo.RpArticleDetailVo;
import org.dromara.rp.domain.RpArticleDetail;
import org.dromara.rp.mapper.RpArticleDetailMapper;
import org.dromara.rp.service.IRpArticleDetailService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.function.Function;

/**
 * 文章任务明细Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@RequiredArgsConstructor
@Service
public class RpArticleDetailServiceImpl implements IRpArticleDetailService {

    private final RpArticleDetailMapper baseMapper;

    private final Function<RpArticleDetailVo, String> getEntityName = (RpArticleDetailVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询文章任务明细
     *
     * @param id 主键
     * @return 文章任务明细
     */
    @Override
    public RpArticleDetailVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询文章任务明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文章任务明细分页列表
     */
    @Override
    public TableDataInfo<RpArticleDetailVo> queryPageList(RpArticleDetailBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpArticleDetail> lqw = buildQueryWrapper(bo);
        Page<RpArticleDetailVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的文章任务明细列表
     *
     * @param bo 查询条件
     * @return 文章任务明细列表
     */
    @Override
    public List<RpArticleDetailVo> queryList(RpArticleDetailBo bo) {
        LambdaQueryWrapper<RpArticleDetail> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpArticleDetail> buildQueryWrapper(RpArticleDetailBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpArticleDetail> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpArticleDetail::getId);
        lqw.eq(bo.getTaskId() != null, RpArticleDetail::getTaskId, bo.getTaskId());
        lqw.eq(bo.getAccountId() != null, RpArticleDetail::getAccountId, bo.getAccountId());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), RpArticleDetail::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), RpArticleDetail::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), RpArticleDetail::getContent, bo.getContent());
        lqw.eq(bo.getPublishStatus() != null, RpArticleDetail::getPublishStatus, bo.getPublishStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getExtraInfo()), RpArticleDetail::getExtraInfo, bo.getExtraInfo());
        return lqw;
    }

    /**
     * 新增文章任务明细
     *
     * @param bo 文章任务明细
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpArticleDetail(RpArticleDetailBo bo) {
        RpArticleDetail add = MapstructUtils.convert(bo, RpArticleDetail.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改文章任务明细
     *
     * @param bo 文章任务明细
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpArticleDetail(RpArticleDetailBo bo) {
        RpArticleDetail update = MapstructUtils.convert(bo, RpArticleDetail.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpArticleDetail entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除文章任务明细信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 批量导入文章任务明细数据
     * @param rpArticleDetailList 文章任务明细列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpArticleDetail(List<RpArticleDetailVo> rpArticleDetailList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpArticleDetailList,
            updateSupport,
            operatorName,
            this::importRpArticleDetailInfo,
            getEntityName
        );
    }

    /**
     * 导入文章任务明细信息
     * @param rpArticleDetailVo 文章任务明细信息
     * @param operatorName 操作人
     */
    private void importRpArticleDetailInfo(RpArticleDetailVo rpArticleDetailVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpArticleDetailBo rpArticleDetailBo = BeanUtil.toBean(rpArticleDetailVo, RpArticleDetailBo.class);
        if (rpArticleDetailBo.getId() == null){
            ValidatorUtils.validate(rpArticleDetailBo, AddGroup.class);
            this.insertRpArticleDetail(rpArticleDetailBo);
        }else {
            ValidatorUtils.validate(rpArticleDetailBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpArticleDetail rpArticleDetail = Db.getById(rpArticleDetailBo.getId(), RpArticleDetail.class);
            if (rpArticleDetail == null){
                rpArticleDetailBo.setId(null);
                this.insertRpArticleDetail(rpArticleDetailBo);
                return;
            }
            this.updateRpArticleDetail(rpArticleDetailBo);
        }
    }
}
