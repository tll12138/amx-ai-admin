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
import org.dromara.rp.domain.vo.RpBitAccountVo;
import org.dromara.rp.domain.bo.RpBitAccountBo;
import org.dromara.rp.domain.RpBitAccount;
import org.dromara.rp.service.IRpBitAccountService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 比特账号信息
 *
 * @author LL
 * @date 2026-03-02
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/bitAccount")
public class RpBitAccountController extends BaseController {

    private final IRpBitAccountService rpBitAccountService;

    /**
     * 查询比特账号信息列表
     */
    @SaCheckPermission("rp:bitAccount:list")
    @GetMapping("/list")
    public TableDataInfo<RpBitAccountVo> list(RpBitAccountBo searchVO, PageQuery pageQuery) {
        return rpBitAccountService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出比特账号信息列表
     */
    @SaCheckPermission("rp:bitAccount:export")
    @Log(title = "比特账号信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpBitAccountBo rpBitAccount, HttpServletResponse response) {
        List<RpBitAccountVo> list = rpBitAccountService.queryList(rpBitAccount);
        ExcelUtil.exportExcel(list, "比特账号信息", RpBitAccountVo.class, response);
    }

    /**
     * 获取比特账号信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:bitAccount:query")
    @GetMapping("/{id}")
    public R<RpBitAccountVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpBitAccountService.queryById(id));
    }

    /**
     * 新增比特账号信息
     */
    @SaCheckPermission("rp:bitAccount:add")
    @Log(title = "比特账号信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpBitAccountBo bo) {
        return toAjax(rpBitAccountService.insertRpBitAccount(bo));
    }

    /**
     * 修改比特账号信息
     */
    @SaCheckPermission("rp:bitAccount:edit")
    @Log(title = "比特账号信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpBitAccountBo bo) {
        return toAjax(rpBitAccountService.updateRpBitAccount(bo));
    }

    /**
     * 删除比特账号信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:bitAccount:remove")
    @Log(title = "比特账号信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpBitAccountService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "比特账号信息数据", RpBitAccountVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:bitAccount:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<RpBitAccountVo> dataList = ExcelUtil.importExcel(file.getInputStream(), RpBitAccountVo.class);
        String operatorName = getUsername();
        String message = rpBitAccountService.importRpBitAccount(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
