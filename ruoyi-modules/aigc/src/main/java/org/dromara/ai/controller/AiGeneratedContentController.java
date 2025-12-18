package org.dromara.ai.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.domain.bo.AiGeneratedContentBo;
import org.dromara.ai.domain.vo.AiGeneratedContentVo;
import org.dromara.ai.service.IAiGeneratedContentService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.dromara.common.satoken.utils.LoginHelper.getUsername;

/**
 * AI生成内容解析
 *
 * @author LL
 * @date 2025-12-18
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/lon/generatedContent")
public class AiGeneratedContentController extends BaseController {

    private final IAiGeneratedContentService aiGeneratedContentService;

    /**
     * 查询AI生成内容解析列表
     */
    @SaCheckPermission("lon:generatedContent:list")
    @GetMapping("/list")
    public TableDataInfo<AiGeneratedContentVo> list(AiGeneratedContentBo searchVO, PageQuery pageQuery) {
        return aiGeneratedContentService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出AI生成内容解析列表
     */
    @SaCheckPermission("lon:generatedContent:export")
    @Log(title = "AI生成内容解析", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiGeneratedContentBo aiGeneratedContent, HttpServletResponse response) {
        List<AiGeneratedContentVo> list = aiGeneratedContentService.queryList(aiGeneratedContent);
        ExcelUtil.exportExcel(list, "AI生成内容解析", AiGeneratedContentVo.class, response);
    }

    /**
     * 获取AI生成内容解析详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("lon:generatedContent:query")
    @GetMapping("/{id}")
    public R<AiGeneratedContentVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiGeneratedContentService.queryById(id));
    }

    /**
     * 新增AI生成内容解析
     */
    @SaCheckPermission("lon:generatedContent:add")
    @Log(title = "AI生成内容解析", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiGeneratedContentBo bo) {
        return toAjax(aiGeneratedContentService.insertAiGeneratedContent(bo));
    }

    /**
     * 修改AI生成内容解析
     */
    @SaCheckPermission("lon:generatedContent:edit")
    @Log(title = "AI生成内容解析", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiGeneratedContentBo bo) {
        return toAjax(aiGeneratedContentService.updateAiGeneratedContent(bo));
    }

    /**
     * 删除AI生成内容解析
     *
     * @param ids 主键串
     */
    @SaCheckPermission("lon:generatedContent:remove")
    @Log(title = "AI生成内容解析", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiGeneratedContentService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "AI生成内容解析数据", AiGeneratedContentVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("lon:generatedContent:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiGeneratedContentVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiGeneratedContentVo.class);
        String operatorName = getUsername();
        String message = aiGeneratedContentService.importAiGeneratedContent(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
