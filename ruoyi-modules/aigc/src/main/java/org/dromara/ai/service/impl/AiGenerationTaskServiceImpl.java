package org.dromara.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.domain.AiGenerationTask;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.AiModel;
import org.dromara.ai.domain.AiPromptStyle;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.domain.vo.AiGenerationTaskVo;
import org.dromara.ai.domain.vo.AiTaskHistoryVo;
import org.dromara.ai.mapper.AiGenerationTaskMapper;
import org.dromara.ai.mapper.AiGoodsMapper;
import org.dromara.ai.service.IAiGenerationTaskService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AI生成任务Service业务层处理
 *
 * @author ZRL
 * @date 2025-03-26
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AiGenerationTaskServiceImpl implements IAiGenerationTaskService {

    private final AiGenerationTaskMapper baseMapper;

    private final AiGoodsMapper aiGoodsMapper;

    /**
     * 查询AI生成任务
     *
     * @param id 主键
     * @return AI生成任务
     */
    @Override
    public AiGenerationTaskVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI生成任务列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI生成任务分页列表
     */
    @Override
    public TableDataInfo<AiGenerationTaskVo> queryPageList(AiGenerationTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiGenerationTask> lqw = buildQueryWrapper(bo);
        Page<AiGenerationTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI生成任务列表
     *
     * @param bo 查询条件
     * @return AI生成任务列表
     */
    @Override
    public List<AiGenerationTaskVo> queryList(AiGenerationTaskBo bo) {
        LambdaQueryWrapper<AiGenerationTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiGenerationTask> buildQueryWrapper(AiGenerationTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiGenerationTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(AiGenerationTask::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getStyle()), AiGenerationTask::getStyle, bo.getStyle());
        lqw.eq(StringUtils.isNotBlank(bo.getModel()), AiGenerationTask::getModel, bo.getModel());
        lqw.eq(StringUtils.isNotBlank(bo.getNoteId()), AiGenerationTask::getNoteId, bo.getNoteId());
        lqw.eq(StringUtils.isNotBlank(bo.getXsecToken()), AiGenerationTask::getXsecToken, bo.getXsecToken());
        lqw.eq(StringUtils.isNotBlank(bo.getNoteCover()), AiGenerationTask::getNoteCover, bo.getNoteCover());
        lqw.eq(StringUtils.isNotBlank(bo.getNoteContent()), AiGenerationTask::getNoteContent, bo.getNoteContent());
        lqw.eq(StringUtils.isNotBlank(bo.getProductId()), AiGenerationTask::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getCompetitorIds()), AiGenerationTask::getCompetitorIds, bo.getCompetitorIds());
        lqw.eq(StringUtils.isNotBlank(bo.getReferenceLatitude()), AiGenerationTask::getReferenceLatitude, bo.getReferenceLatitude());
        lqw.eq(StringUtils.isNotBlank(bo.getOtherLimit()), AiGenerationTask::getOtherLimit, bo.getOtherLimit());
        lqw.eq(StringUtils.isNotBlank(bo.getAiContent()), AiGenerationTask::getAiContent, bo.getAiContent());
        lqw.eq(StringUtils.isNotBlank(bo.getOperationType()), AiGenerationTask::getOperationType, bo.getOperationType());
        lqw.eq(StringUtils.isNotBlank(bo.getGenerateStatus()), AiGenerationTask::getGenerateStatus, bo.getGenerateStatus());
        lqw.eq(bo.getGrade() != null, AiGenerationTask::getGrade, bo.getGrade());
        return lqw;
    }

    /**
     * 新增AI生成任务
     *
     * @param bo AI生成任务
     * @return 是否新增成功
     */
    @Override
    public String insertAiGenerationTask(AiGenerationTaskBo bo) {
        List<String> keywords = bo.getKeywords();
        AiGenerationTask add = MapstructUtils.convert(bo, AiGenerationTask.class);
        if (CollectionUtil.isNotEmpty(keywords) && add != null) {
            add.setKeywords(StringUtils.join(keywords, ","));
        }
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
            return add.getId();
        }
        return "";
    }

    /**
     * 修改AI生成任务
     *
     * @param bo AI生成任务
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiGenerationTask(AiGenerationTaskBo bo) {
        AiGenerationTask update = MapstructUtils.convert(bo, AiGenerationTask.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     * 校验内容：必填字段、关联数据存在性、唯一约束
     */
    private void validEntityBeforeSave(AiGenerationTask entity) {
        // 1. 必填字段校验
        if (entity.getType() == null) {
            throw new ServiceException("任务类型不能为空");
        }
        if (StringUtils.isBlank(entity.getStyle())) {
            throw new ServiceException("创作风格不能为空");
        }
        if (StringUtils.isBlank(entity.getModel())) {
            throw new ServiceException("AI模型不能为空");
        }
        if (StringUtils.isBlank(entity.getGenerateStatus())) {
            throw new ServiceException("生成状态不能为空");
        }

        // 2. 关联数据存在性校验
        AiPromptStyle style = Db.getById(entity.getStyle(), AiPromptStyle.class);
        if (style == null) {
            throw new ServiceException("创作风格不存在，请检查");
        }
        AiModel model = Db.getById(entity.getModel(), AiModel.class);
        if (model == null) {
            throw new ServiceException("AI模型不存在，请检查");
        }
        if (StringUtils.isNotBlank(entity.getProductId())) {
            AiGoods product = Db.getById(entity.getProductId(), AiGoods.class);
            if (product == null) {
                throw new ServiceException("关联产品不存在，请检查");
            }
        }

        // 3. 唯一约束校验
        LambdaQueryWrapper<AiGenerationTask> wrapper = Wrappers.lambdaQuery();
        // 仅当 productId 不为空时，添加产品ID条件
        if (StringUtils.isNotBlank(entity.getProductId())) {
            wrapper.eq(AiGenerationTask::getProductId, entity.getProductId());
        }
        // 模型和风格为必填项，直接添加条件
        wrapper.eq(AiGenerationTask::getModel, entity.getModel())
            .eq(AiGenerationTask::getStyle, entity.getStyle());
        // 编辑场景：当ID不为空时，排除当前记录
        if (StringUtils.isNotBlank(entity.getId())) {
            wrapper.ne(AiGenerationTask::getId, entity.getId());
        }

        // 查询是否存在重复记录
        AiGenerationTask exist = baseMapper.selectOne(wrapper);
        if (exist != null) {
            throw new ServiceException("同一产品、模型和风格的任务已存在，请勿重复创建");
        }
    }

    /**
     * 校验并批量删除AI生成任务信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<AiTaskHistoryVo> queryAll() {
        // 获取当前登录用户ID
        Long userId = LoginHelper.getUserId();
        MPJLambdaWrapper<AiGenerationTask> wrapper = new MPJLambdaWrapper<AiGenerationTask>();
        wrapper.selectAll(AiGenerationTask.class)
            .selectAll(AiGenerationTask.class)
            .selectAs(AiPromptStyle::getName, AiTaskHistoryVo::getStyleName)
            .selectAs(AiModel::getName, AiTaskHistoryVo::getModelName)
            .selectAs(AiGoods::getName, AiTaskHistoryVo::getProductName)
            .leftJoin(AiPromptStyle.class, AiPromptStyle::getId, AiGenerationTask::getStyle)
            .leftJoin(AiModel.class, AiModel::getId, AiGenerationTask::getModel)
            .leftJoin(AiGoods.class, AiGoods::getId, AiGenerationTask::getProductId)
            .eq(AiGenerationTask::getCreateBy, userId)
            .orderByDesc(AiGenerationTask::getCreateTime);
        return baseMapper.selectJoinList(AiTaskHistoryVo.class, wrapper);
    }

    @Override
    public List<String> getCompetitors(String id) {

        if (StringUtils.isBlank(id)) {
            return Collections.emptyList();
        }
        AiGenerationTask aiGenerationTask = baseMapper.selectById(id);
        if (aiGenerationTask == null) {
            return Collections.emptyList();
        }
        String competitorIds = aiGenerationTask.getCompetitorIds();
        if (StringUtils.isBlank(competitorIds)) {
            return Collections.emptyList();
        }
        List<String> competitorIdList = Arrays.asList(competitorIds.split(","));
        LambdaQueryWrapper<AiGoods> in = Wrappers.lambdaQuery(AiGoods.class)
            .select(AiGoods::getName)
            .in(AiGoods::getId, competitorIdList);
        return aiGoodsMapper.selectObjs(in);
    }

    public static AiBaseBo convertModifyVo2TaskBo(AiGenerationTaskBo modifyVo) {
        AiBaseBo taskBo = new AiBaseBo();
        taskBo.setModel(modifyVo.getModel());
        taskBo.setStyle(modifyVo.getStyle());
        taskBo.setSystemMessage(handleModifySystemMessage(modifyVo));
        taskBo.setUserMessage(handleModifyUserMessage(modifyVo));
        taskBo.setTemperature(modifyVo.getTemperature());
        taskBo.setTopP(modifyVo.getTopP());
        return taskBo;
    }

    public static String handleModifySystemMessage(AiGenerationTaskBo taskBo) {
        String style = taskBo.getStyle();
        if (StringUtils.isEmpty(style)) {
            return "";
        }
        AiPromptStyle aiPromptStyle = Db.getById(style, AiPromptStyle.class);
        if (aiPromptStyle == null) {
            return "";
        }
        String systemMessage = aiPromptStyle.getPrompt();
        log.info("系统信息:{}", systemMessage);
        return systemMessage;
    }

    public static String handleModifyUserMessage(AiGenerationTaskBo taskBo) {
        String productId = taskBo.getProductId(); // 产品ID
        if (StringUtils.isEmpty(productId)) {
            throw new ServiceException("产品不能为空");
        }
        AiGoods product = Db.getById(productId, AiGoods.class);
        if (product == null) {
            throw new ServiceException("产品不存在");
        }

        String basePromptTemplate =
            """
                请你根据此产品信息:{}；以及原文信息：{}；
                根据下面的要求修改原文中的此部分内容：{}；
                返回结果：只需返回修改后的此段的内容即可，不需要返回其他段落和额外的说明内容；
                """;
        String userMessage = StrUtil.format(
            basePromptTemplate,
            product.toString(),
            taskBo.getAiContent(),
            taskBo.getOtherLimit()
        );
        log.info("用户信息：{}", userMessage);
        return userMessage;
    }

}
