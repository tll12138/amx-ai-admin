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
import org.dromara.rp.domain.bo.RpArticleTaskBo;
import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.RpArticleTask;
import org.dromara.rp.mapper.RpArticleTaskMapper;
import org.dromara.rp.service.IRpArticleTaskService;
import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.function.Function;

/**
 * 文章任务主Service业务层处理
 *
 * @author ZRL
 * @date 2025-11-13
 */
@RequiredArgsConstructor
@Service
public class RpArticleTaskServiceImpl implements IRpArticleTaskService {

    private final RpArticleTaskMapper baseMapper;

    private final Function<RpArticleTaskVo, String> getEntityName = (RpArticleTaskVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询文章任务主
     *
     * @param id 主键
     * @return 文章任务主
     */
    @Override
    public RpArticleTaskVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询文章任务主列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 文章任务主分页列表
     */
    @Override
    public TableDataInfo<RpArticleTaskVo> queryPageList(RpArticleTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<RpArticleTask> lqw = buildQueryWrapper(bo);
        Page<RpArticleTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的文章任务主列表
     *
     * @param bo 查询条件
     * @return 文章任务主列表
     */
    @Override
    public List<RpArticleTaskVo> queryList(RpArticleTaskBo bo) {
        LambdaQueryWrapper<RpArticleTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<RpArticleTask> buildQueryWrapper(RpArticleTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<RpArticleTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(RpArticleTask::getId);
        lqw.like(StringUtils.isNotBlank(bo.getTaskName()), RpArticleTask::getTaskName, bo.getTaskName());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), RpArticleTask::getDescription, bo.getDescription());
        lqw.eq(bo.getTotalArticles() != null, RpArticleTask::getTotalArticles, bo.getTotalArticles());
        lqw.eq(bo.getStatus() != null, RpArticleTask::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增文章任务主
     *
     * @param bo 文章任务主
     * @return 是否新增成功
     */
    @Override
    public Boolean insertRpArticleTask(RpArticleTaskBo bo) {
        RpArticleTask add = MapstructUtils.convert(bo, RpArticleTask.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改文章任务主
     *
     * @param bo 文章任务主
     * @return 是否修改成功
     */
    @Override
    public Boolean updateRpArticleTask(RpArticleTaskBo bo) {
        RpArticleTask update = MapstructUtils.convert(bo, RpArticleTask.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(RpArticleTask entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除文章任务主信息
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
     * 批量导入文章任务主数据
     * @param rpArticleTaskList 文章任务主列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importRpArticleTask(List<RpArticleTaskVo> rpArticleTaskList, boolean updateSupport, String operatorName){
        return ImportEntities(
            rpArticleTaskList,
            updateSupport,
            operatorName,
            this::importRpArticleTaskInfo,
            getEntityName
        );
    }

    /**
     * 导入文章任务主信息
     * @param rpArticleTaskVo 文章任务主信息
     * @param operatorName 操作人
     */
    private void importRpArticleTaskInfo(RpArticleTaskVo rpArticleTaskVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        RpArticleTaskBo rpArticleTaskBo = BeanUtil.toBean(rpArticleTaskVo, RpArticleTaskBo.class);
        if (rpArticleTaskBo.getId() == null){
            ValidatorUtils.validate(rpArticleTaskBo, AddGroup.class);
            this.insertRpArticleTask(rpArticleTaskBo);
        }else {
            ValidatorUtils.validate(rpArticleTaskBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            RpArticleTask rpArticleTask = Db.getById(rpArticleTaskBo.getId(), RpArticleTask.class);
            if (rpArticleTask == null){
                rpArticleTaskBo.setId(null);
                this.insertRpArticleTask(rpArticleTaskBo);
                return;
            }
            this.updateRpArticleTask(rpArticleTaskBo);
        }
    }
}
