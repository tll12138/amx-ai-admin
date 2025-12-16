package org.dromara.ai.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.ai.domain.option.BaseOptions;
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
import org.dromara.ai.domain.vo.AiPromptStyleVo;
import org.dromara.ai.domain.bo.AiPromptStyleBo;
import org.dromara.ai.service.IAiPromptStyleService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * AI内容风格
 *
 * @author ZRL
 * @date 2025-04-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/promptStyle")
public class AiPromptStyleController extends BaseController {

    private final IAiPromptStyleService aiPromptStyleService;

    /**
     * 查询AI内容风格列表
     */
    @SaCheckPermission("ai:promptStyle:list")
    @GetMapping("/list")
    public TableDataInfo<AiPromptStyleVo> list(AiPromptStyleBo searchVO, PageQuery pageQuery) {
        return aiPromptStyleService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出AI内容风格列表
     */
    @SaCheckPermission("ai:promptStyle:export")
    @Log(title = "AI内容风格", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiPromptStyleBo aiPromptStyle, HttpServletResponse response) {
        List<AiPromptStyleVo> list = aiPromptStyleService.queryList(aiPromptStyle);
        ExcelUtil.exportExcel(list, "AI内容风格", AiPromptStyleVo.class, response);
    }

    /**
     * 获取AI内容风格详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:promptStyle:query")
    @GetMapping("/{id}")
    public R<AiPromptStyleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiPromptStyleService.queryById(id));
    }

    /**
     * 新增AI内容风格
     */
    @SaCheckPermission("ai:promptStyle:add")
    @Log(title = "AI内容风格", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<String> add(@Validated(AddGroup.class) @RequestBody AiPromptStyleBo bo) {
        return R.ok("操作成功", aiPromptStyleService.insertAiPromptStyle(bo));
    }

    /**
     * 修改AI内容风格
     */
    @SaCheckPermission("ai:promptStyle:edit")
    @Log(title = "AI内容风格", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiPromptStyleBo bo) {
        return toAjax(aiPromptStyleService.updateAiPromptStyle(bo));
    }

    /**
     * 删除AI内容风格
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:promptStyle:remove")
    @Log(title = "AI内容风格", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiPromptStyleService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "AI内容风格数据", AiPromptStyleVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("ai:promptStyle:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiPromptStyleVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiPromptStyleVo.class);
        String operatorName = getUsername();
        String message = aiPromptStyleService.importAiPromptStyle(dataList, updateSupport, operatorName);
        return R.ok(message);
    }

    /**
     * 获取AI模型配置详细信息
     * @return AI模型配置详细信息
     */
    @GetMapping("/options")
    public R<List<AiPromptStyleVo>> getOptions(@RequestParam("type") String type) {
        return R.ok(aiPromptStyleService.getStyleOptions(type));
    }
}
