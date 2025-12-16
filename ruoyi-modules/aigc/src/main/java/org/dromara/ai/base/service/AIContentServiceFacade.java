package org.dromara.ai.base.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.base.exception.ModelNotFoundException;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.AiModel;
import org.dromara.ai.domain.AiPromptStyle;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.enums.TaskTypeEnum;
import org.dromara.common.core.exception.ServiceException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIContentServiceFacade {

    private final AIBaseService baseService;

    public Flux<String> streamGenerate(AiGenerationTaskBo taskBo) {
        String modelId = taskBo.getModel();
        TaskTypeEnum type = taskBo.getType();
        if (modelId == null) {
            throw new ModelNotFoundException("无效的模型信息");
        }
        // 查询模型信息
        AiModel model = Db.getById(modelId, AiModel.class);
        if (model == null) {
            throw new ModelNotFoundException(modelId);  // 模型未找到
        }
        Double temperature = taskBo.getTemperature();
        Double topP = taskBo.getTopP();
        if (temperature != null){
            model.setTemperature(temperature);
        }
        if (topP != null){
            model.setTopP(topP);
        }

        Map<String, Object> prompt = new HashMap<>();
        prompt.put("messages", switch (type) {
            case ZHONG_CAO -> handleZCUserMessage(taskBo); // 种草
            case HENG_CE -> handleHCUserMessage(taskBo); // 横测
            default -> throw new ServiceException("无效的生成类型");
        });
        prompt.put("system", handleSystemMessage(taskBo));

        // 调用策略的流式生成方法，并解析返回结果
        return baseService.streamGenerate(model, prompt);
    }

    public Flux<String> chatGenerate(AiBaseBo taskBo, boolean jsonType) {
        String modelId = taskBo.getModel();
        if (modelId == null) {
            throw new ModelNotFoundException("无效的模型信息");
        }
        // 查询模型信息
        AiModel model = Db.getById(modelId, AiModel.class);
        if (model == null) {
            throw new ModelNotFoundException(modelId);  // 模型未找到
        }
        Double temperature = taskBo.getTemperature();
        Double topP = taskBo.getTopP();
        if (temperature != null){
            model.setTemperature(temperature);
        }
        if (topP != null){
            model.setTopP(topP);
        }

        Map<String, Object> prompt = new HashMap<>();
        prompt.put("system", taskBo.getSystemMessage());
        prompt.put("messages", taskBo.getUserMessage());
        if (jsonType){
            log.info("jsonType");
//            prompt.put("response_format", Map.of("type", "json_object"));
        }

        // 调用策略的流式生成方法，并解析返回结果
        try {
            String res = baseService.chatGenerate(model, prompt);
            log.info("直接返回结果：{}", res);
            return Flux.just(res);
        }catch (Exception e){
            log.error("调用失败：{}", e.getMessage());
            if (e.getMessage().contains("only support stream mode")){
                return Flux.error(new ServiceException("暂不支持当前模型，请选择其他模型"));
            }
            return Flux.error(e);
        }
    }
    public String chat(AiBaseBo taskBo, boolean jsonType) {
        // 模型
        AiModel model = Db.getOne(
            Wrappers.lambdaQuery(AiModel.class)
                .eq(AiModel::getModel, "deepseek-chat")
                .eq(AiModel::getProvider, "deepseek")
        );
        Double temperature = taskBo.getTemperature();
        Double topP = taskBo.getTopP();
        if (temperature != null){
            model.setTemperature(temperature);
        }
        if (topP != null){
            model.setTopP(topP);
        }
        taskBo.setModel(model.getModel());

        Map<String, Object> prompt = new HashMap<>();
        prompt.put("system", taskBo.getSystemMessage());
        prompt.put("messages", taskBo.getUserMessage());
        if (jsonType){
            prompt.put("response_format", Map.of("type", "json_object"));
        }

        // 调用策略的流式生成方法，并解析返回结果
        try {
            String res = baseService.chatGenerate(model, prompt);
            log.info("直接返回结果：{}", res);
            AIChunkResponse bean = JSONUtil.toBean(res, AIChunkResponse.class);
            return bean.getContent();
        }catch (Exception e){
            log.error("调用失败：{}", e.getMessage());
            if (e.getMessage().contains("only support stream mode")){
                throw new ServiceException("暂不支持当前模型，请选择其他模型");
            }
            throw e;
        }
    }

    public String directChatGenerateComment(AiBaseBo taskBo, AiModel model) {
        Map<String, Object> prompt = new HashMap<>();
        prompt.put("system", taskBo.getSystemMessage());
        prompt.put("messages", taskBo.getUserMessage());
        prompt.put("response_format", Map.of("type", "json_object"));
        // 调用策略的流式生成方法，并解析返回结果
        try {
            String res = baseService.chatGenerate(model, prompt);
            log.info("直接返回结果：{}", res);
            return res;
        }catch (Exception e){
            log.error("调用失败：{}", e.getMessage());
            if (e.getMessage().contains("only support stream mode")){
                return "";
            }
            return "";
        }
    }

    public String handleHCUserMessage(AiGenerationTaskBo request) {
        AiGoods goods = Db.getOne(Wrappers.lambdaQuery(AiGoods.class).eq(AiGoods::getId, request.getProductId()));
        if (goods == null) {
            throw new ServiceException("无效的商品信息");
        }

        String noteContent = request.getNoteContent();
        List<String> keywords = request.getKeywords();

        String message;
        if (StrUtil.isEmpty(noteContent)) {
            // 原创模式
            message = StrUtil.format(
                "请你写一篇小红书的「测评」笔记内容，书写测评文案并隐晦突出此产品，此产品信息为：{}；",
                goods.toString()
            );
        } else {
            // 仿写模式
            message = StrUtil.format(
                "请你仿写一篇小红书的「测评」笔记内容，仿照参考文章来写出测评文案并隐晦突出此产品，此产品信息为：{}；",
                goods.toString()
            );
        }
        // 添加竞品信息
        String competitorIds = request.getCompetitorIds();
        if (StrUtil.isNotEmpty(competitorIds)) {
            String latitude = request.getReferenceLatitude();
            List<String> competitorIdList = List.of(competitorIds.split(","));
            List<AiGoods> list = Db.list(
                Wrappers.lambdaQuery(AiGoods.class)
                    .in(CollectionUtil.isNotEmpty(competitorIdList), AiGoods::getId, competitorIdList));
            String competitors = list.stream().map(AiGoods::toString).collect(Collectors.joining(";"));
            latitude = StrUtil.isEmpty(latitude) ? "全部" : latitude;
            message += StrUtil.format("\n另外还需要对竞品的信息，对比竞品的维度为：{}维度；"
                + "竞品信息为：{}；根据这些进行更好的测评文案输出", latitude, competitors);
        }


        // 添加关键词埋点（同时适用于原创和仿写模式）
        if (CollectionUtil.isNotEmpty(keywords)) {
            String keywordInstruction = StrUtil.format(
                "\n此外请您将这些关键词融入「正文」中用来提高其他用户的搜索（每个必须至少出现1次），根据语义选择融入到标签还是正文中：{}。"
                    + "注意：1.保持语句通顺 2.不要集中出现 3.重要关键词可酌情重复",
                String.join("、", keywords)
            );
            message += keywordInstruction;
        }

        // 增加其他要求部分
        message += StrUtil.format("\n标题控制在20字内！这是其他重点要求！！：{}；",
            request.getOtherLimit()
        );

        // 最后添加仿写内容
        if (StrUtil.isNotEmpty(noteContent)){
            message += StrUtil.format(
                "最后你需要先分析仿写文章的标题结构、内容框架和标签使用，保持相似风格；这是需仿写内容：【{}】",
                noteContent
            );
        }

        log.info("最终提示词：{}", message);
        return message;
    }

    public String handleZCUserMessage(AiGenerationTaskBo request) {
        AiGoods goods = Db.getOne(Wrappers.lambdaQuery(AiGoods.class).eq(AiGoods::getId, request.getProductId()));
        if (goods == null) {
            throw new ServiceException("无效的商品信息");
        }

        String noteContent = request.getNoteContent();
        List<String> keywords = request.getKeywords();
        String basePrompt = "隐晦的推荐我们的产品，产品信息为：{}；"
            + "结合小红书的写作风格，以及产品的卖点、成分、功效等进行{}；"
            + "标题控制在20字内！其他重点要求：{}；";

        String message;
        if (StrUtil.isEmpty(noteContent)) {
            // 原创模式
            message = StrUtil.format("请你写一篇小红书的「种草」笔记内容；" + basePrompt,
                goods.toString(),
                "输出",
                request.getOtherLimit());
        } else {
            // 仿写模式
            message = StrUtil.format("请你仿写一篇小红书的「种草」笔记内容；" + basePrompt,
                goods.toString(),
                "文章仿写",
                request.getOtherLimit());
        }

        // 添加关键词埋点（同时适用于原创和仿写模式）
        if (CollectionUtil.isNotEmpty(keywords)) {
            String keywordInstruction = StrUtil.format(
                "\n此外请您将这些自然关键词融入「正文」中用来提高其他用户的搜索（每个必须至少出现1次），根据语义选择融入到标签还是正文中：{}。"
                    + "注意：1.保持语句通顺 2.不要集中出现 3.重要关键词可酌情重复",
                String.join("、", keywords)
            );
            message += keywordInstruction;
        }

        // 最后添加仿写内容
        if (StrUtil.isNotEmpty(noteContent)){
            message += StrUtil.format(
                "最后你需要先分析仿写文章的标题结构、内容框架和标签使用，保持相似风格；这是需仿写内容：【{}】",
                noteContent
            );
        }

        log.info("最终提示词：{}", message);
        return message;
    }

    public String handleSystemMessage(AiGenerationTaskBo request) {
        String styleId = request.getStyle();
        AiPromptStyle style = Db.getById(styleId, AiPromptStyle.class);
        log.info("style:{}", style);
        if (style == null || StrUtil.isEmpty(style.getPrompt())) {
            return "";
        }
        return style.getPrompt();
    }
}
