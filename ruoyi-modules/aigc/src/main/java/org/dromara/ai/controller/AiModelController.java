package org.dromara.ai.controller;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.service.AIBaseService;
import org.dromara.ai.base.vo.AIChunkResponse;
import org.dromara.ai.domain.AiModel;
import org.dromara.ai.domain.option.BaseOptions;
import org.dromara.ai.domain.option.ModelOptions;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.ai.domain.vo.AiModelVo;
import org.dromara.ai.domain.bo.AiModelBo;
import org.dromara.ai.service.IAiModelService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * AI模型配置
 *
 * @author ZRL
 * @date 2025-04-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/ai/model")
public class AiModelController extends BaseController {

    private final IAiModelService aiModelService;

    private final AIBaseService aiBaseService;

    /**
     * 查询AI模型配置列表
     */
    @SaCheckPermission("ai:model:list")
    @GetMapping("/list")
    public TableDataInfo<AiModelVo> list(AiModelBo searchVO, PageQuery pageQuery) {
        return aiModelService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出AI模型配置列表
     */
    @SaCheckPermission("ai:model:export")
    @Log(title = "AI模型配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiModelBo aiModel, HttpServletResponse response) {
        List<AiModelVo> list = aiModelService.queryList(aiModel);
        ExcelUtil.exportExcel(list, "AI模型配置", AiModelVo.class, response);
    }

    /**
     * 获取AI模型配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:model:query")
    @GetMapping("/{id}")
    public R<AiModelVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiModelService.queryById(id));
    }

    /**
     * 新增AI模型配置
     */
    @SaCheckPermission("ai:model:add")
    @Log(title = "AI模型配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiModelBo bo) {
        return toAjax(aiModelService.insertAiModel(bo));
    }

    /**
     * 修改AI模型配置
     */
    @SaCheckPermission("ai:model:edit")
    @Log(title = "AI模型配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiModelBo bo) {
        return toAjax(aiModelService.updateAiModel(bo));
    }

    /**
     * 删除AI模型配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:model:remove")
    @Log(title = "AI模型配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiModelService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "AI模型配置数据", AiModelVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("ai:model:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiModelVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiModelVo.class);
        String operatorName = getUsername();
        String message = aiModelService.importAiModel(dataList, updateSupport, operatorName);
        return R.ok(message);
    }

    /**
     * 获取AI模型配置详细信息
     * @return AI模型配置详细信息
     */
    @GetMapping("/options")
    public R<List<ModelOptions>> getModelOptions() {
        return R.ok(aiModelService.getModelOptions());
    }


    @PostMapping("/test")
    public R<String> TestModelConnect(@Validated(AddGroup.class) @RequestBody AiModelBo modelBo) {
        try {
            AiModel model = MapstructUtils.convert(modelBo, AiModel.class);
            String res = aiBaseService.testChat(model,
                "[Test] Please return the following text as quickly as possible, without thinking or adding content: AI service connected" );
            AIChunkResponse bean = JSONUtil.toBean(res, AIChunkResponse.class);
            log.info("测试结果：{}", bean.toString());
            return StrUtil.isNotEmpty(bean.getContent()) ? R.ok("连接测试成功") : R.fail("连接测试失败");
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains(":")){
                message = message.split(":",2)[1];
            }
            log.error("测试连接失败：{}", e.getMessage());
            return R.fail("连接测试失败：" + message);
        }
    }
}
