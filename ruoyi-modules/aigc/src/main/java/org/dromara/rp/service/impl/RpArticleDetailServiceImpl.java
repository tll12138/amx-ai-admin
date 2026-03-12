package org.dromara.rp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.rp.domain.RpArticleDetail;
import org.dromara.rp.domain.RpArticleTask;
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.rp.domain.bo.RpArticleDetailCallbackBo;
import org.dromara.rp.domain.vo.RpArticleDetailVo;
import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.mapper.RpAccountMapper;
import org.dromara.rp.mapper.RpArticleDetailMapper;
import org.dromara.rp.mapper.RpArticleTaskMapper;
import org.dromara.rp.mapper.RpaAccountConfigMapper;
import org.dromara.rp.service.IRpArticleDetailService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

/**
 * 文章任务明细Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class RpArticleDetailServiceImpl implements IRpArticleDetailService {

    private final RpArticleDetailMapper baseMapper;
    private final RpArticleTaskMapper taskMapper;
    private final RpaAccountConfigMapper accountConfigMapper;
    private final RpAccountMapper rpAccountMapper;

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
        // 1. 原有的分页查询文章列表
        LambdaQueryWrapper<RpArticleDetail> lqw = buildQueryWrapper(bo);
        Page<RpArticleDetailVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        List<RpArticleDetailVo> records = result.getRecords();

        // 2. 收集所有非空的 accountId
        List<Long> accountIds = records.stream()
            .map(RpArticleDetailVo::getAccountId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        // 3. 批量查询账号配置，并转为 Map<id, robotClientName>
        if (!accountIds.isEmpty()) {
            Map<Long, String> accountNameMap = accountConfigMapper.selectVoByIds(accountIds)
                .stream()
                .filter(vo -> vo.getRobotClientName() != null) // 过滤空值防止 NPE
                .collect(Collectors.toMap(
                    RpaAccountConfigVo::getId,
                    RpaAccountConfigVo::getRobotClientName,
                    (v1, v2) -> v1 // 防止重复 key 冲突
                ));

            // 4. 遍历文章列表，从 Map 中取值赋值
            records.forEach(item -> {
                if (item.getAccountId() != null) {
                    String accountName = accountNameMap.get(item.getAccountId());
                    item.setAccountName(accountName);
                }
            });
        }

        result.setRecords(records);
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
     * 根据主任务id获取该任务下所有详情
     *
     * @param mainId
     * @return
     */
    @Override
    public List<RpArticleDetailVo> queryMainDetailList(Long mainId) {
        return baseMapper.selectVoList(new LambdaQueryWrapper<RpArticleDetail>().eq(RpArticleDetail::getTaskId, mainId));
    }

    /**
     * 回调文章任务明细
     */
    @Override
    public void callback(RpArticleDetailCallbackBo bo) {
        log.info("回调文章任务明细: {}", bo);
        Long id = bo.getId();
        if (id == null) {
            return;
        }
        RpArticleDetail rpArticleDetail = baseMapper.selectById(id);
        if (rpArticleDetail == null){
            return;
        }
        rpArticleDetail.setPublishStatus("success".equals(bo.getStatus())?2L:3L);
        rpArticleDetail.setUpdateTime(new Date());
        rpArticleDetail.setPostScreenshot(bo.getImg());
        baseMapper.updateById(rpArticleDetail);
        RpArticleTaskVo rpArticleTaskVo = taskMapper.selectVoById(rpArticleDetail.getTaskId());
        rpArticleTaskVo.setStatus("success".equals(bo.getStatus())?2L:3L);
        taskMapper.updateById(BeanUtil.copyProperties(rpArticleTaskVo, RpArticleTask.class));
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
