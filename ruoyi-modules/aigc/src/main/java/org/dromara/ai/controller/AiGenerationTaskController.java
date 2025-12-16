package org.dromara.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.UUID;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.service.AIContentServiceFacade;
import org.dromara.ai.domain.bo.AiGenerationTaskBo;
import org.dromara.ai.domain.vo.AiGenerationTaskVo;
import org.dromara.ai.domain.vo.AiTaskHistoryVo;
import org.dromara.ai.service.IAiGenerationTaskService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

import static org.dromara.ai.service.impl.AiGenerationTaskServiceImpl.convertModifyVo2TaskBo;

/**
 * AI生成任务
 *
 * @author ZRL
 * @date 2025-03-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/generationTask")
@Slf4j
public class AiGenerationTaskController extends BaseController {


    private final AIContentServiceFacade aiService;

    private final IAiGenerationTaskService aiGenerationTaskService;

    /**
     * SSE流式生成入口
     * @param request 包含模型ID和用户输入的请求体
     * @return SSE格式的流式响应
     */
    @PostMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> handleSSEStream(@RequestBody AiGenerationTaskBo request) {
        return aiService.streamGenerate(request)
            // 将原始数据流包装为SSE事件
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
    /**
     * chat
     * @param request 包含模型ID和用户输入的请求体
     * @return 修改后的内容
     */
    @PostMapping(value = "/modifyContent")
    public R<String> handleModifyContent(@RequestBody AiGenerationTaskBo request) {
        return R.ok("修改成功",aiService.chat(convertModifyVo2TaskBo(request), false));
    }

    /**
     * SSE心跳保活（防止连接超时）
     */
    private Flux<ServerSentEvent<String>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
            .map(seq -> ServerSentEvent.<String>builder()
                .event("heartbeat")
                .data("{\"beat\": " + seq + "}")
                .build());
    }

    /**
     * 查询AI生成任务列表
     */
    @SaCheckPermission("ai:generationTask:list")
    @GetMapping("/list")
    public TableDataInfo<AiGenerationTaskVo> list(AiGenerationTaskBo searchVO, PageQuery pageQuery) {
        return aiGenerationTaskService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 查询AI生成任务列表
     */
    @SaCheckPermission("ai:generationTask:list")
    @GetMapping("/all")
    public R<List<AiTaskHistoryVo>> getAll() {
        return R.ok(aiGenerationTaskService.queryAll());
    }
    /**
     * 查询AI生成任务列表
     */
    @SaCheckPermission("ai:generationTask:list")
    @GetMapping("/competitors")
    public R<List<String>> getCompetitors(@RequestParam("id") String id) {
        return R.ok(aiGenerationTaskService.getCompetitors(id));
    }

    /**
     * 导出AI生成任务列表
     */
    @SaCheckPermission("ai:generationTask:export")
    @Log(title = "AI生成任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiGenerationTaskBo aiGenerationTask, HttpServletResponse response) {
        List<AiGenerationTaskVo> list = aiGenerationTaskService.queryList(aiGenerationTask);
        ExcelUtil.exportExcel(list, "AI生成任务", AiGenerationTaskVo.class, response);
    }

    /**
     * 获取AI生成任务详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:generationTask:query")
    @GetMapping("/{id}")
    public R<AiGenerationTaskVo> getInfo(@NotNull(message = "主键不能为空")
                                         @PathVariable String id) {
        return R.ok(aiGenerationTaskService.queryById(id));
    }

    /**
     * 新增AI生成任务
     */
    @SaCheckPermission("ai:generationTask:add")
    @Log(title = "AI生成任务", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<String> add(@Validated(AddGroup.class) @RequestBody AiGenerationTaskBo bo) {
        return R.ok("ok",aiGenerationTaskService.insertAiGenerationTask(bo));
    }

    /**
     * 修改AI生成任务
     */
    @SaCheckPermission("ai:generationTask:edit")
    @Log(title = "AI生成任务", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping("/updateParagraph")
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiGenerationTaskBo bo) {
        return toAjax(aiGenerationTaskService.updateAiGenerationTask(bo));
    }

    /**
     * 删除AI生成任务
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:generationTask:remove")
    @Log(title = "AI生成任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiGenerationTaskService.deleteWithValidByIds(List.of(ids), true));
    }
}
