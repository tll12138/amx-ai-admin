package org.dromara.xhs.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.ai.domain.vo.AiGoodsVo;
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
import org.dromara.xhs.domain.vo.XhsShareNoteVo;
import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.xhs.domain.XhsShareNote;
import org.dromara.xhs.service.IXhsShareNoteService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 小红书笔记链接分享
 *
 * @author ZRL
 * @date 2025-08-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xhs/shareNote")
public class XhsShareNoteController extends BaseController {

    private final IXhsShareNoteService xhsShareNoteService;

    /**
     * 查询小红书笔记链接分享列表
     */
    @SaCheckPermission("xhs:shareNote:list")
    @GetMapping("/list")
    public TableDataInfo<XhsShareNoteVo> list(XhsShareNoteBo searchVO, PageQuery pageQuery) {
        return xhsShareNoteService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出小红书笔记链接分享列表
     */
    @SaCheckPermission("xhs:shareNote:export")
    @Log(title = "小红书笔记链接分享", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(XhsShareNoteBo xhsShareNote, HttpServletResponse response) {
        List<XhsShareNoteVo> list = xhsShareNoteService.queryList(xhsShareNote);
        ExcelUtil.exportExcel(list, "小红书笔记链接分享", XhsShareNoteVo.class, response);
    }

    /**
     * 获取小红书笔记链接分享详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("xhs:shareNote:query")
    @GetMapping("/{id}")
    public R<XhsShareNoteVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(xhsShareNoteService.queryById(id));
    }

    /**
     * 新增小红书笔记链接分享
     */
    @SaCheckPermission("xhs:shareNote:add")
    @Log(title = "小红书笔记链接分享", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody XhsShareNoteBo bo) {
        return toAjax(xhsShareNoteService.insertXhsShareNote(bo));
    }

    /**
     * 修改小红书笔记链接分享
     */
    @SaCheckPermission("xhs:shareNote:edit")
    @Log(title = "小红书笔记链接分享", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody XhsShareNoteBo bo) {
        return toAjax(xhsShareNoteService.updateXhsShareNote(bo));
    }

    /**
     * 删除小红书笔记链接分享
     *
     * @param ids 主键串
     */
    @SaCheckPermission("xhs:shareNote:remove")
    @Log(title = "小红书笔记链接分享", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(xhsShareNoteService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 批量更新
     **/
    @SaCheckPermission("xhs:shareNote:edit")
    @GetMapping("/batchUpdate")
    public R<Boolean> batchUpdate() {
        xhsShareNoteService.batchUpdate();
        return R.ok("触发更新成功..正在更新中");
    }

    /**
     * 批量更新
     **/
    @SaCheckPermission("xhs:shareNote:edit")
    @GetMapping("/updateInteraction/{noteId}")
    public R<String> UpdateInteractionByNoteId(@PathVariable("noteId") String noteId) {

        return R.ok(xhsShareNoteService.UpdateInteractionByNoteId(noteId));
    }



    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "小红书笔记链接分享数据", XhsShareNoteBo.class, response);
    }

    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("xhs:shareNote:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<XhsShareNoteBo> dataList = ExcelUtil.importExcel(file.getInputStream(), XhsShareNoteBo.class);
        String operatorName = getUsername();
        String message = xhsShareNoteService.importXhsShareNote(dataList, updateSupport, operatorName);
        return R.ok(message);
    }


}
