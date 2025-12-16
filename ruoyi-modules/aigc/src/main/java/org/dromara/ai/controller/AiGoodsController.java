package org.dromara.ai.controller;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.ai.domain.vo.AiProductKeywordsVo;
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
import org.dromara.ai.domain.vo.AiGoodsVo;
import org.dromara.ai.domain.bo.AiGoodsBo;
import org.dromara.ai.domain.AiGoods;
import org.dromara.ai.service.IAiGoodsService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 产品信息
 *
 * @author ZRL
 * @date 2025-03-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/goods")
public class AiGoodsController extends BaseController {

    private final IAiGoodsService aiGoodsService;

    /**
     * 查询产品信息列表
     */
    @SaCheckPermission("ai:goods:list")
    @GetMapping("/list")
    public TableDataInfo<AiGoodsVo> list(AiGoodsBo searchVO, PageQuery pageQuery) {
        return aiGoodsService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出产品信息列表
     */
    @SaCheckPermission("ai:goods:export")
    @Log(title = "产品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(AiGoodsBo aiGoods, HttpServletResponse response) {
        List<AiGoodsVo> list = aiGoodsService.queryList(aiGoods);
        ExcelUtil.exportExcel(list, "产品信息", AiGoodsVo.class, response);
    }

    /**
     * 获取产品信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("ai:goods:query")
    @GetMapping("/{id}")
    public R<AiGoodsVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(aiGoodsService.queryById(id));
    }



    /**
     * 新增产品信息
     */
    @SaCheckPermission("ai:goods:add")
    @Log(title = "产品信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiGoodsBo bo) {
        return toAjax(aiGoodsService.insertAiGoods(bo));
    }

    /**
     * 修改产品信息
     */
    @SaCheckPermission("ai:goods:edit")
    @Log(title = "产品信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiGoodsBo bo) {
        return toAjax(aiGoodsService.updateAiGoods(bo));
    }

    /**
     * 删除产品信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("ai:goods:remove")
    @Log(title = "产品信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(aiGoodsService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "商品数据", AiGoodsVo.class, response);
    }

    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importKeywordsTemplate")
    public void importKeywordsTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "商品关键词数据", AiProductKeywordsVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("ai:goods:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiGoodsVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiGoodsVo.class);
        String operatorName = getUsername();
        String message = aiGoodsService.importAiGoods(dataList, updateSupport, operatorName);
        return R.ok(message);
    }

    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("ai:goods:add")
    @PostMapping("/importKeywordsData")
    public R<String> importKeywordsData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<AiProductKeywordsVo> dataList = ExcelUtil.importExcel(file.getInputStream(), AiProductKeywordsVo.class);
        String operatorName = getUsername();
        String message = aiGoodsService.importAiGoodsKeywords(dataList, updateSupport, operatorName);
        return R.ok(message);
    }

    @GetMapping("/getSelfGoods")
    public R<JSONArray> getSelfGoods() {
        JSONArray res = aiGoodsService.getSelfGoods();
        return R.ok(res);
    }

    /**
     * 根据产品获取竞品的信息
     * @param id 产品ID
     * @return 竞品信息列表
     */
    @GetMapping("/getCompetitorOptions/{id}")
    public R<JSONArray> getCompetitorOptions(@NotNull(message = "商品ID不能为空") @PathVariable String id) {
        JSONArray res = aiGoodsService.getCompetitorOptions(id);
        return R.ok(res);
    }
}
