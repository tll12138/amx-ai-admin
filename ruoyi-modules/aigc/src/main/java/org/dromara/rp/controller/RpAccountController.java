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
import org.dromara.rp.domain.vo.RpAccountVo;
import org.dromara.rp.domain.bo.RpAccountBo;
import org.dromara.rp.domain.RpAccount;
import org.dromara.rp.service.IRpAccountService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 账号信息
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/account")
public class RpAccountController extends BaseController {

    private final IRpAccountService rpAccountService;

    /**
     * 查询账号信息列表
     */
    @SaCheckPermission("rp:account:list")
    @GetMapping("/list")
    public TableDataInfo<RpAccountVo> list(RpAccountBo searchVO, PageQuery pageQuery) {
        return rpAccountService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出账号信息列表
     */
    @SaCheckPermission("rp:account:export")
    @Log(title = "账号信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpAccountBo rpAccount, HttpServletResponse response) {
        List<RpAccountVo> list = rpAccountService.queryList(rpAccount);
        ExcelUtil.exportExcel(list, "账号信息", RpAccountVo.class, response);
    }

    /**
     * 获取账号信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:account:query")
    @GetMapping("/{id}")
    public R<RpAccountVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpAccountService.queryById(id));
    }

    /**
     * 新增账号信息
     */
    @SaCheckPermission("rp:account:add")
    @Log(title = "账号信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpAccountBo bo) {
        return toAjax(rpAccountService.insertRpAccount(bo));
    }

    /**
     * 修改账号信息
     */
    @SaCheckPermission("rp:account:edit")
    @Log(title = "账号信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpAccountBo bo) {
        return toAjax(rpAccountService.updateRpAccount(bo));
    }

    /**
     * 删除账号信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:account:remove")
    @Log(title = "账号信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpAccountService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "账号信息数据", RpAccountVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:account:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<RpAccountVo> dataList = ExcelUtil.importExcel(file.getInputStream(), RpAccountVo.class);
        String operatorName = getUsername();
        String message = rpAccountService.importRpAccount(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
