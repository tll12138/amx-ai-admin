package org.dromara.rp.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.core.utils.dropdown.common.DropDownOptionsGroup;
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
import org.dromara.rp.domain.vo.RpAccountGroupVo;
import org.dromara.rp.domain.vo.RpAccountGroupWithAccountsVo;
import org.dromara.rp.domain.bo.RpAccountGroupBo;
import org.dromara.rp.service.IRpAccountGroupService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 账号分组
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/accountGroup")
public class RpAccountGroupController extends BaseController {

    private final IRpAccountGroupService rpAccountGroupService;

    /**
     * 查询账号分组列表
     */
    @SaCheckPermission("rp:accountGroup:list")
    @GetMapping("/list")
    public TableDataInfo<RpAccountGroupVo> list(RpAccountGroupBo searchVO, PageQuery pageQuery) {
        return rpAccountGroupService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 获取账号分组下拉列表
     */
    @GetMapping("/options")
    public R<List<DropDownOptionsGroup<Long>>> selectList() {
        List<DropDownOptionsGroup<Long>> list = rpAccountGroupService.getSelectOptions();
        return R.ok(list);
    }

    @SaCheckPermission("rp:accountGroup:list")
    @GetMapping("/groupsWithAccounts")
    public R<List<RpAccountGroupWithAccountsVo>> groupsWithAccounts(@NotBlank(message = "平台不能为空") @RequestParam("platform") String platform) {
        List<RpAccountGroupWithAccountsVo> list = rpAccountGroupService.listGroupsWithAccountsByPlatform(platform);
        return R.ok(list);
    }

    /**
     * 导出账号分组列表
     */
    @SaCheckPermission("rp:accountGroup:export")
    @Log(title = "账号分组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpAccountGroupBo rpAccountGroup, HttpServletResponse response) {
        List<RpAccountGroupVo> list = rpAccountGroupService.queryList(rpAccountGroup);
        ExcelUtil.exportExcel(list, "账号分组", RpAccountGroupVo.class, response);
    }

    /**
     * 获取账号分组详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:accountGroup:query")
    @GetMapping("/{id}")
    public R<RpAccountGroupVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpAccountGroupService.queryById(id));
    }

    /**
     * 新增账号分组
     */
    @SaCheckPermission("rp:accountGroup:add")
    @Log(title = "账号分组", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpAccountGroupBo bo) {
        return toAjax(rpAccountGroupService.insertRpAccountGroup(bo));
    }

    /**
     * 修改账号分组
     */
    @SaCheckPermission("rp:accountGroup:edit")
    @Log(title = "账号分组", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpAccountGroupBo bo) {
        return toAjax(rpAccountGroupService.updateRpAccountGroup(bo));
    }

    /**
     * 删除账号分组
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:accountGroup:remove")
    @Log(title = "账号分组", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpAccountGroupService.deleteWithValidByIds(List.of(ids), true));
    }
}
