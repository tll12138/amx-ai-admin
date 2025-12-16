package org.dromara.rp.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
import org.dromara.rp.domain.vo.RpArticleDetailVo;
import org.dromara.rp.domain.bo.RpArticleDetailBo;
import org.dromara.rp.domain.RpArticleDetail;
import org.dromara.rp.service.IRpArticleDetailService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 文章任务明细
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/articleDetail")
public class RpArticleDetailController extends BaseController {

    private final IRpArticleDetailService rpArticleDetailService;

    /**
     * 查询文章任务明细列表
     */
    @SaCheckPermission("rp:articleDetail:list")
    @GetMapping("/list")
    public TableDataInfo<RpArticleDetailVo> list(RpArticleDetailBo searchVO, PageQuery pageQuery) {
        return rpArticleDetailService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出文章任务明细列表
     */
    @SaCheckPermission("rp:articleDetail:export")
    @Log(title = "文章任务明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpArticleDetailBo rpArticleDetail, HttpServletResponse response) {
        List<RpArticleDetailVo> list = rpArticleDetailService.queryList(rpArticleDetail);
        ExcelUtil.exportExcel(list, "文章任务明细", RpArticleDetailVo.class, response);
    }

    /**
     * 获取文章任务明细详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:articleDetail:query")
    @GetMapping("/{id}")
    public R<RpArticleDetailVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpArticleDetailService.queryById(id));
    }

    /**
     * 新增文章任务明细
     */
    @SaCheckPermission("rp:articleDetail:add")
    @Log(title = "文章任务明细", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpArticleDetailBo bo) {
        return toAjax(rpArticleDetailService.insertRpArticleDetail(bo));
    }

    /**
     * 修改文章任务明细
     */
    @SaCheckPermission("rp:articleDetail:edit")
    @Log(title = "文章任务明细", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpArticleDetailBo bo) {
        return toAjax(rpArticleDetailService.updateRpArticleDetail(bo));
    }

    /**
     * 删除文章任务明细
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:articleDetail:remove")
    @Log(title = "文章任务明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpArticleDetailService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "文章任务明细数据", RpArticleDetailVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:articleDetail:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<RpArticleDetailVo> dataList = ExcelUtil.importExcel(file.getInputStream(), RpArticleDetailVo.class);
        String operatorName = getUsername();
        String message = rpArticleDetailService.importRpArticleDetail(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
