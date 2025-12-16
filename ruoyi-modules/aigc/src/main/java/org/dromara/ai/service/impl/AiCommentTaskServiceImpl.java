package org.dromara.ai.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.base.service.AIContentServiceFacade;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.dromara.ai.domain.AiCommentTask;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.AiGoodsRelations;
import org.dromara.ai.domain.AiPromptStyle;
import org.dromara.ai.domain.bo.AiCommentTaskBo;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.domain.vo.AiCommentTaskVo;
import org.dromara.ai.enums.GuidelineEnum;
import org.dromara.ai.enums.SentimentEnum;
import org.dromara.ai.mapper.AiCommentTaskMapper;
import org.dromara.ai.service.IAiCommentTaskService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * AI评论生成Service业务层处理
 *
 * @author ZRL
 * @date 2025-05-07
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AiCommentTaskServiceImpl implements IAiCommentTaskService {

    private final AIContentServiceFacade aiService;

    private final AiCommentTaskMapper baseMapper;

    /**
     * 查询AI评论生成
     *
     * @param id 主键
     * @return AI评论生成
     */
    @Override
    public AiCommentTaskVo queryById(String id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询AI评论生成列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return AI评论生成分页列表
     */
    @Override
    public TableDataInfo<AiCommentTaskVo> queryPageList(AiCommentTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<AiCommentTask> lqw = buildQueryWrapper(bo);
        Page<AiCommentTaskVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的AI评论生成列表
     *
     * @param bo 查询条件
     * @return AI评论生成列表
     */
    @Override
    public List<AiCommentTaskVo> queryList(AiCommentTaskBo bo) {
        LambdaQueryWrapper<AiCommentTask> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<AiCommentTask> buildQueryWrapper(AiCommentTaskBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<AiCommentTask> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(AiCommentTask::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getType()), AiCommentTask::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getStyle()), AiCommentTask::getStyle, bo.getStyle());
        lqw.eq(StringUtils.isNotBlank(bo.getModel()), AiCommentTask::getModel, bo.getModel());
        lqw.eq(StringUtils.isNotBlank(bo.getExample()), AiCommentTask::getExample, bo.getExample());
        lqw.eq(StringUtils.isNotBlank(bo.getProductId()), AiCommentTask::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getSentiment()), AiCommentTask::getSentiment, bo.getSentiment());
        lqw.eq(StringUtils.isNotBlank(bo.getGuideline()), AiCommentTask::getGuideline, bo.getGuideline());
        lqw.eq(StringUtils.isNotBlank(bo.getKeywords()), AiCommentTask::getKeywords, bo.getKeywords());
        lqw.eq(bo.getCommentCount() != null, AiCommentTask::getCommentCount, bo.getCommentCount());
        lqw.eq(bo.getMaxWords() != null, AiCommentTask::getMaxWords, bo.getMaxWords());
        lqw.eq(StringUtils.isNotBlank(bo.getAiContent()), AiCommentTask::getAiContent, bo.getAiContent());
        lqw.eq(StringUtils.isNotBlank(bo.getOperationType()), AiCommentTask::getOperationType, bo.getOperationType());
        lqw.eq(StringUtils.isNotBlank(bo.getGenerateStatus()), AiCommentTask::getGenerateStatus, bo.getGenerateStatus());
        return lqw;
    }

    /**
     * 新增AI评论生成
     *
     * @param bo AI评论生成
     * @return 是否新增成功
     */
    @Override
    public String insertAiCommentTask(AiCommentTaskBo bo) {
        AiCommentTask add = MapstructUtils.convert(bo, AiCommentTask.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return add.getId();
    }

    /**
     * 修改AI评论生成
     *
     * @param bo AI评论生成
     * @return 是否修改成功
     */
    @Override
    public Boolean updateAiCommentTask(AiCommentTaskBo bo) {
        AiCommentTask update = MapstructUtils.convert(bo, AiCommentTask.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(AiCommentTask entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除AI评论生成信息
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

/*    @Override
    public Flux<ServerSentEvent<String>> chat(AiCommentTaskBo aiCommentTaskBo) {
        return aiService.chatGenerate(this.convert(aiCommentTaskBo))
            .<String>handle((data, sink) -> {
                AIChunkResponse bean = JSONUtil.toBean(data, AIChunkResponse.class);
                String content = bean.getContent();
                // 从res截取[]包含的所有内容
                int start = content.indexOf("[");
                int end = content.lastIndexOf("]");
                if (start == -1 || end == -1) {
                    sink.error(new ServiceException("AI生成失败请重新尝试"));
                    return;
                }
                content = content.substring(start, end + 1);
                log.info("处理后结果：{}", content);
                sink.next(content);
            })
            .map(data -> ServerSentEvent.<String>builder()
                .data(data)
                .event("message")
                .id(UUID.fastUUID().toString())
                .build())
            // 捕获错误，发送错误事件并终止流
            .onErrorResume(error -> {
                log.error("SSE流式生成发生错误：{}", error.getMessage());
                ServerSentEvent<String> errorEvent = ServerSentEvent.<String>builder()
                    .event("error")
                    .data(error.getMessage())
                    .build();
                return Flux.just(errorEvent); // 发送错误事件后终止流
            });
    }*/

    public static AiBaseBo convert2TaskDO(AiCommentTaskBo aiCommentTaskBo) {
        AiBaseBo aiBaseBo = new AiBaseBo();
        aiBaseBo.setStyle(aiCommentTaskBo.getStyle());
        aiBaseBo.setModel(aiCommentTaskBo.getModel());
        aiBaseBo.setSystemMessage(handleSystemMessage(aiCommentTaskBo));
        aiBaseBo.setUserMessage(handleUserMessage(aiCommentTaskBo));
        aiBaseBo.setTemperature(aiCommentTaskBo.getTemperature());
        aiBaseBo.setTopP(aiCommentTaskBo.getTopP());
        return aiBaseBo;
    }

    public static String handleUserMessage(AiCommentTaskBo aiCommentTaskBo) {
        String productId = aiCommentTaskBo.getProductId(); // 产品ID
        if (StringUtils.isEmpty(productId)) {
            throw new ServiceException("产品不能为空");
        }
        AiGoods product = Db.getById(productId, AiGoods.class);
        if (product == null){
            throw new ServiceException("产品不存在");
        }

        String example = aiCommentTaskBo.getExample(); // 参考示例
        String guideline = aiCommentTaskBo.getGuideline(); // 引导
        GuidelineEnum guidelineEnum = GuidelineEnum.getByKey(guideline);
        String sentiment = aiCommentTaskBo.getSentiment(); // 情感
        SentimentEnum sentimentEnum = SentimentEnum.getByKey(sentiment);
        Long maxWords = aiCommentTaskBo.getMaxWords(); // 最大字数
        String keywords = aiCommentTaskBo.getKeywords(); // 关键词
        Long count = aiCommentTaskBo.getCommentCount(); // 条数
        String basePromptTemplate =
                """
                请你根据此产品信息来生成真实的用户评论内容:{}；
                强制要求:
                1.输出格式：必须只有一个字符串列表（JSON格式）请严格按照JSON格式输出，不要包含额外内容，不要使用markdown输出。
                2.输出示例格式如：{"comments":['示例格式评论xxx1','示例格式评论xxx2']}, 全部放在一个列表内每个元素为去除换行符后的字符串;
                创作要求:
                1.生成评论数：{}个；即列表中有且只有{}项；
                2.情感倾向:{}
                3.引导方式：{}
                4.每个评论最多{}个字；
                5.如果参考示例存在，请你根据仿照参考示例的评论内容生成评论
                """;
        String userMessage = StrUtil.format(basePromptTemplate, product.toString(), count, count,
            sentimentEnum.getMean(),
            guidelineEnum.getMean(),
            maxWords
        );
        if (StringUtils.isNotEmpty(keywords)) userMessage +=  "关键词：请你适当出现以下关键词【" + keywords + "】\n";
        if (StringUtils.isNotEmpty(example)) userMessage +=  "参考示例：" + example + '\n';
        log.info("用户信息：{}", userMessage);
        return userMessage;
    }

    public static String handleSystemMessage(AiCommentTaskBo aiCommentTaskBo) {
        String style = aiCommentTaskBo.getStyle();
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
}
