package org.dromara.ai.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.ai.domain.option.BaseGroupOption;
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
import org.dromara.ai.domain.vo.AiKeywordsVo;
import org.dromara.ai.domain.bo.AiKeywordsBo;
import org.dromara.ai.domain.AiKeywords;
import org.dromara.ai.service.IAiKeywordsService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 关键词
 *
 * @author ZRL
 * @date 2025-04-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/keywords")
public class AiKeywordsController extends BaseController {

    private final IAiKeywordsService aiKeywordsService;

    /**
     * 查询关键词列表
     */
    @SaCheckPermission("ai:keywords:list")
    @GetMapping("/list")
    public TableDataInfo<AiKeywordsVo> list(AiKeywordsBo searchVO, PageQuery pageQuery) {
        return aiKeywordsService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 获取某商品的所有关键词内容
     * @param productId 产品id
     * @return options
     */
    @SaCheckPermission("ai:keywords:list")
    @GetMapping("/option")
    public R<List<BaseGroupOption>> getKeyWordsOptionByProductId(@RequestParam("productId")String productId) {
        return R.ok(aiKeywordsService.getProductKeywordsOptions(productId));
    }

    /**
     * 导出关键词列表
     */
    @SaCheckPermission("ai:keywords:export")
    @Log(title = "关键词", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiKeywordsBo aiKeywords, HttpServletResponse response) {
        List<AiKeywordsVo> list = aiKeywordsService.queryList(aiKeywords);
        ExcelUtil.exportExcel(list, "关键词", AiKeywordsVo.class, response);
    }

    /**
     * 获取关键词详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:keywords:query")
    @GetMapping("/{id}")
    public R<AiKeywordsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiKeywordsService.queryById(id));
    }

    /**
     * 新增关键词
     */
    @SaCheckPermission("ai:keywords:add")
    @Log(title = "关键词", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiKeywordsBo bo) {
        return toAjax(aiKeywordsService.insertAiKeywords(bo));
    }

    /**
     * 修改关键词
     */
    @SaCheckPermission("ai:keywords:edit")
    @Log(title = "关键词", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiKeywordsBo bo) {
        return toAjax(aiKeywordsService.updateAiKeywords(bo));
    }

    /**
     * 删除关键词
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:keywords:remove")
    @Log(title = "关键词", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiKeywordsService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "关键词数据", AiKeywordsVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("ai:keywords:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiKeywordsVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiKeywordsVo.class);
        String operatorName = getUsername();
        String message = aiKeywordsService.importAiKeywords(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
