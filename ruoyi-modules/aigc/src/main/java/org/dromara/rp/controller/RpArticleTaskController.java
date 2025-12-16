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
import org.dromara.rp.domain.vo.RpArticleTaskVo;
import org.dromara.rp.domain.bo.RpArticleTaskBo;
import org.dromara.rp.domain.RpArticleTask;
import org.dromara.rp.service.IRpArticleTaskService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 文章任务主
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/articleTask")
public class RpArticleTaskController extends BaseController {

    private final IRpArticleTaskService rpArticleTaskService;

    /**
     * 查询文章任务主列表
     */
    @SaCheckPermission("rp:articleTask:list")
    @GetMapping("/list")
    public TableDataInfo<RpArticleTaskVo> list(RpArticleTaskBo searchVO, PageQuery pageQuery) {
        return rpArticleTaskService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出文章任务主列表
     */
    @SaCheckPermission("rp:articleTask:export")
    @Log(title = "文章任务主", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpArticleTaskBo rpArticleTask, HttpServletResponse response) {
        List<RpArticleTaskVo> list = rpArticleTaskService.queryList(rpArticleTask);
        ExcelUtil.exportExcel(list, "文章任务主", RpArticleTaskVo.class, response);
    }

    /**
     * 获取文章任务主详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:articleTask:query")
    @GetMapping("/{id}")
    public R<RpArticleTaskVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpArticleTaskService.queryById(id));
    }

    /**
     * 新增文章任务主
     */
    @SaCheckPermission("rp:articleTask:add")
    @Log(title = "文章任务主", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpArticleTaskBo bo) {
        return toAjax(rpArticleTaskService.insertRpArticleTask(bo));
    }

    /**
     * 修改文章任务主
     */
    @SaCheckPermission("rp:articleTask:edit")
    @Log(title = "文章任务主", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpArticleTaskBo bo) {
        return toAjax(rpArticleTaskService.updateRpArticleTask(bo));
    }

    /**
     * 删除文章任务主
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:articleTask:remove")
    @Log(title = "文章任务主", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpArticleTaskService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "文章任务主数据", RpArticleTaskVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:articleTask:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<RpArticleTaskVo> dataList = ExcelUtil.importExcel(file.getInputStream(), RpArticleTaskVo.class);
        String operatorName = getUsername();
        String message = rpArticleTaskService.importRpArticleTask(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
