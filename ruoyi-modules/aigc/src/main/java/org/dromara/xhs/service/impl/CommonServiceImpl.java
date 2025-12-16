package org.dromara.xhs.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.bo.AiBaseBo;
import org.dromara.ai.base.service.AIContentServiceFacade;
import org.dromara.xhs.config.XhsConfig;
import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.xhs.domain.vo.common.TransformContentReqVO;
import org.dromara.xhs.domain.vo.common.TransformContentRespVO;
import org.dromara.xhs.domain.vo.common.TransformImageReqVO;
import org.dromara.xhs.service.CommonService;
import org.dromara.xhs.service.IXhsShareNoteService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author zrl
 * @date 2025/7/30
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CommonServiceImpl implements CommonService {

    private final XhsConfig xhsConfig;

    private final AIContentServiceFacade aiContentServiceFacade;

    private final IXhsShareNoteService xhsShareNoteService
    ;

    @Override
    public List<String> transformImages(TransformImageReqVO reqVO) {
        List<String> images = reqVO.getImages();
        if (CollectionUtil.isEmpty(images)) {
            return Collections.emptyList();
        }
        log.info("开始转换图片");

        // 创建线程池（可根据机器核数或请求量调整）
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(images.size(), 10));

        // 存放每个异步任务
        List<CompletableFuture<String>> futureList = new ArrayList<>();

        for (String imgUrl : images) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    // 调用远程接口，返回新图片地址
                    return doTransformImage(imgUrl);
                } catch (Exception e) {
                    log.error("图片处理失败: {}", imgUrl, e);
                    return imgUrl; // 或者返回默认图、原图
                }
            }, executor);
            futureList.add(future);
        }

        // 等待所有任务完成，收集结果
        List<String> result = futureList.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        log.info("转换完成，结果：{}", result);
        executor.shutdown();
        return result;
    }

    private String doTransformImage(String imgUrl) {
        // 示例URL构建（可从配置读取服务地址）
        HttpRequest post = HttpUtil.createPost(xhsConfig.getTransformImageUrl());
        post.header("Content-Type", ContentType.MULTIPART.getValue());
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("img", imgUrl);
        formMap.put("type", "colorize");
        formMap.put("arg1", 80);
        formMap.put("arg2", 80);
        formMap.put("arg3", 80);
        formMap.put("arg4", 80);
        post.form(formMap);
        // 执行请求
        HttpResponse response = post.execute();

        // 获取返回内容
        String body = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = null;
        try {
            root = objectMapper.readTree(body);
            // 判断响应是否成功
            if (root.path("code").asInt() == 0) {
                JsonNode dataNode = root.path("data");
                if (dataNode.isArray() && !dataNode.isEmpty()) {
                    JsonNode firstArray = dataNode.get(0);
                    if (firstArray.isArray() && !firstArray.isEmpty()) {
                        return firstArray.get(0).asText();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("图片处理失败: {}", imgUrl, e);
            throw new RuntimeException(e);
        }
        throw new RuntimeException("图片处理失败或响应格式错误");
    }

    @Override
    public TransformContentRespVO generateContent(TransformContentReqVO reqVO) {
        AiBaseBo aiBaseBo = new AiBaseBo();
        String content = reqVO.getContent();
        String userMessage = "请优化下面的内容：" + content;
        aiBaseBo.setSystemMessage(
            """
                你是一个熟悉小红书平台内容风格的文案优化专家，精通平台的运营规则、社区氛围和算法偏好。
                你理解小红书用户更喜欢真实、有共鸣、有细节、有“人味”的表达方式，反感生硬的营销语、空洞无物的描述，尤其排斥看起来像 AI 生成的套话。
                你的任务是：帮助用户润色他们提供的文案，使其在不失真实性的前提下变得更加吸引人、具有视觉化画面感和传播力；善于用小红书用户熟悉的语言方式表达亮点，比如加入生活场景描写、细节体验、感受变化等，传递“种草感”或“生活感”而不是硬广。
                同时处理标题部分，标题放在首行与内容进行区分，切记标题不可超过20字。不需要给出任何其他建议类内容，直接返回优化后的文案即可；
                返回的json格式为：{ title: "文案标题部分", content: "文案内容部分" }
                请优化用户提供的内容，注意控制语气自然不过度，避免平台敏感词，遵循平台调性，使内容更具吸引力而不过度营销或失真。
                """);
        aiBaseBo.setUserMessage(userMessage);
        String responseContent = aiContentServiceFacade.chat(aiBaseBo, true);
        log.info("responseContent: {}", responseContent);
        TransformContentRespVO transformContentRespVO = JSONUtil.toBean(responseContent, TransformContentRespVO.class);
        log.info("transformContentRespVO: {}", transformContentRespVO);
        return transformContentRespVO;
    }

    @Override
    public Boolean addNote(XhsShareNoteBo shareNoteBo) {
        return xhsShareNoteService.insertXhsShareNote(shareNoteBo);
    }
}
