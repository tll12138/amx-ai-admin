package org.dromara.ai.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.domain.AiGeneratedContent;
import org.dromara.ai.domain.AiGenerationTask;
import org.dromara.ai.domain.bo.AiGeneratedContentBo;
import org.dromara.ai.domain.vo.AiGeneratedContentVo;
import org.dromara.ai.mapper.AiGeneratedContentMapper;
import org.dromara.ai.service.IAiGeneratedContentService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.dromara.common.core.utils.ExcelUtil.ImportEntities;

/**
 * AI生成内容解析Service业务层处理
 *
 * @author LL
 * @date 2025-12-18
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AiGeneratedContentServiceImpl implements IAiGeneratedContentService {

    private final AiGeneratedContentMapper baseMapper;

    private final Function<AiGeneratedContentVo, String> getEntityName = (AiGeneratedContentVo  entity) -> {
        if (entity == null){
            return "";
        }
        return entity.getId() == null ? "" : entity.getId().toString();
    };
    /**
     * 查询AI生成内容解析
     *
     * @param id 主键
     * @return AI生成内容解析
     */
    @Override
    public AiGeneratedContentVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI生成内容解析列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI生成内容解析分页列表
     */
    @Override
    public TableDataInfo<AiGeneratedContentVo> queryPageList(AiGeneratedContentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiGeneratedContent> lqw = buildQueryWrapper(bo);
        Page<AiGeneratedContentVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI生成内容解析列表
     *
     * @param bo 查询条件
     * @return AI生成内容解析列表
     */
    @Override
    public List<AiGeneratedContentVo> queryList(AiGeneratedContentBo bo) {
        LambdaQueryWrapper<AiGeneratedContent> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiGeneratedContent> buildQueryWrapper(AiGeneratedContentBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiGeneratedContent> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(AiGeneratedContent::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTaskId()), AiGeneratedContent::getTaskId, bo.getTaskId());
        lqw.eq(StringUtils.isNotBlank(bo.getOriginalContent()), AiGeneratedContent::getOriginalContent, bo.getOriginalContent());
        lqw.eq(StringUtils.isNotBlank(bo.getParsedTitle()), AiGeneratedContent::getParsedTitle, bo.getParsedTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getParsedParagraphs()), AiGeneratedContent::getParsedParagraphs, bo.getParsedParagraphs());
        lqw.eq(StringUtils.isNotBlank(bo.getKeywordAnalysis()), AiGeneratedContent::getKeywordAnalysis, bo.getKeywordAnalysis());
        lqw.eq(bo.getGenerateTime() != null, AiGeneratedContent::getGenerateTime, bo.getGenerateTime());
        return lqw;
    }

    /**
     * 新增AI生成内容解析
     *
     * @param bo AI生成内容解析
     * @return 是否新增成功
     */
    @Override
    public Boolean insertAiGeneratedContent(AiGeneratedContentBo bo) {
        AiGeneratedContent add = MapstructUtils.convert(bo, AiGeneratedContent.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改AI生成内容解析
     *
     * @param bo AI生成内容解析
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiGeneratedContent(AiGeneratedContentBo bo) {
        AiGeneratedContent update = MapstructUtils.convert(bo, AiGeneratedContent.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiGeneratedContent entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除AI生成内容解析信息
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
     * 批量导入AI生成内容解析数据
     * @param aiGeneratedContentList AI生成内容解析列表
     * @param updateSupport 是否更新已存在数据
     * @param operatorName 操作人
     * @return 导入结果内容
     */
    @Override
    public String importAiGeneratedContent(List<AiGeneratedContentVo> aiGeneratedContentList, boolean updateSupport, String operatorName){
        return ImportEntities(
            aiGeneratedContentList,
            updateSupport,
            operatorName,
            this::importAiGeneratedContentInfo,
            getEntityName
        );
    }

    /**
     * 解析并保存生成内容
     *
     * @param task 生成任务实体（包含AI生成的内容）
     */
    @Override
    public void parseAndSaveContent(AiGenerationTask task) {
        // 1. 创建解析后的内容实体
        AiGeneratedContent content = new AiGeneratedContent();
        content.setTaskId(task.getId());
        content.setOriginalContent(task.getAiContent());

    }

    /**
     * 导入AI生成内容解析信息
     * @param aiGeneratedContentVo AI生成内容解析信息
     * @param operatorName 操作人
     */
    private void importAiGeneratedContentInfo(AiGeneratedContentVo aiGeneratedContentVo, String operatorName) {
        // TODO 自定义导入逻辑
        // TODO 自定义导入逻辑
        AiGeneratedContentBo aiGeneratedContentBo = BeanUtil.toBean(aiGeneratedContentVo, AiGeneratedContentBo.class);
        if (aiGeneratedContentBo.getId() == null){
            ValidatorUtils.validate(aiGeneratedContentBo, AddGroup.class);
            this.insertAiGeneratedContent(aiGeneratedContentBo);
        }else {
            ValidatorUtils.validate(aiGeneratedContentBo, EditGroup.class);
            // 防止ID找不到或者删除了的情况重新导入
            AiGeneratedContent aiGeneratedContent = Db.getById(aiGeneratedContentBo.getId(), AiGeneratedContent.class);
            if (aiGeneratedContent == null){
                aiGeneratedContentBo.setId(null);
                this.insertAiGeneratedContent(aiGeneratedContentBo);
                return;
            }
            this.updateAiGeneratedContent(aiGeneratedContentBo);
        }
    }
}
