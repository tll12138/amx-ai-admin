package org.dromara.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.service.AIContentServiceFacade;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.domain.bo.AiCommentTaskBo;
import org.dromara.ai.domain.vo.AiCommentExportVo;
import org.dromara.ai.domain.vo.AiCommentTaskVo;
import org.dromara.ai.service.IAiCommentTaskService;
import org.dromara.ai.domain.Comment;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.dromara.ai.service.impl.AiCommentTaskServiceImpl.convert2TaskDO;

/**
 * AI评论生成
 *
 * @author ZRL
 * @date 2025-05-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/ai/commentTask")
public class AiCommentTaskController extends BaseController {


    private final AIContentServiceFacade aiService;

    private final IAiCommentTaskService aiCommentTaskService;


    @PostMapping("/generate")
    public Flux<ServerSentEvent<String>> generate(@RequestBody AiCommentTaskBo aiCommentTaskBo) {
        return aiService.chatGenerate(convert2TaskDO(aiCommentTaskBo), true)
            // 捕获错误，发送错误事件并终止流
            .<String>handle((data, sink) -> {
                log.info("处理前的评论信息：{}", data);
                Comment comments = parseComments(data);
                log.info("处理后评论信息：{}", comments.getComments());
                sink.next(JSONUtil.toJsonStr(comments.getComments()));
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
    }

    @org.jetbrains.annotations.NotNull
    private static Comment parseComments(String data) {
        AIChunkResponse bean = JSONUtil.toBean(data, AIChunkResponse.class);
        if (bean == null){
            throw new RuntimeException("返回结果为空");
        }
        String content = bean.getContent();
        Comment comments = JSONUtil.toBean(content, Comment.class);
        if (comments == null){
            throw new RuntimeException("返回结果为空");
        }
        return comments;
    }


    /**
     * 查询AI评论生成列表
     */
    @SaCheckPermission("ai:commentTask:list")
    @GetMapping("/list")
    public TableDataInfo<AiCommentTaskVo> list(AiCommentTaskBo searchVO, PageQuery pageQuery) {
        return aiCommentTaskService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出AI评论生成列表
     */
    @SaCheckPermission("ai:commentTask:export")
    @Log(title = "AI评论生成", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiCommentTaskBo taskBo, HttpServletResponse response) {
        String id = taskBo.getId();
        if (StrUtil.isEmpty(id)){
            throw new ServiceException("导出失败，请重新生成任务后重试");
        }
        AiCommentTaskVo aiCommentTask = aiCommentTaskService.queryById(id);
        String aiContent = aiCommentTask.getAiContent();
        List<String> list;
        ObjectMapper mapper = new ObjectMapper();
        try {
            list = mapper.readValue(aiContent, List.class);
        } catch (JsonProcessingException e) {
            throw new ServiceException("获取AI评论内容失败");
        }
        AiGoods goods = Db.getById(aiCommentTask.getProductId(), AiGoods.class);
        if (goods == null){
            throw new ServiceException("产品信息异常");
        }
        String name = goods.getName();
        List<AiCommentExportVo> collect = list.stream().map(item -> {
            AiCommentExportVo aiCommentExportVo = new AiCommentExportVo();
            aiCommentExportVo.setName(name);
            aiCommentExportVo.setComment(item);
            return aiCommentExportVo;
        }).collect(Collectors.toList());
        ExcelUtil.exportExcel(collect, "AI评论生成", AiCommentExportVo.class, response);
    }

    /**
     * 获取AI评论生成详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:commentTask:query")
    @GetMapping("/{id}")
    public R<AiCommentTaskVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiCommentTaskService.queryById(id));
    }

    /**
     * 新增AI评论生成
     */
    @SaCheckPermission("ai:commentTask:add")
    @Log(title = "AI评论生成", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<String> add(@Validated(AddGroup.class) @RequestBody AiCommentTaskBo bo) {
        return R.ok("操作成功",aiCommentTaskService.insertAiCommentTask(bo));
    }

    /**
     * 修改AI评论生成
     */
    @SaCheckPermission("ai:commentTask:edit")
    @Log(title = "AI评论生成", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiCommentTaskBo bo) {
        return toAjax(aiCommentTaskService.updateAiCommentTask(bo));
    }

    /**
     * 删除AI评论生成
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:commentTask:remove")
    @Log(title = "AI评论生成", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiCommentTaskService.deleteWithValidByIds(List.of(ids), true));
    }
}
