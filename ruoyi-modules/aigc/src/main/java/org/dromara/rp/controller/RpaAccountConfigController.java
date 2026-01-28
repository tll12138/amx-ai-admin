package org.dromara.rp.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
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
import org.dromara.rp.domain.bo.RpaAccountConfigBo;
import org.dromara.rp.domain.vo.RpaAccountConfigVo;
import org.dromara.rp.service.IRpaAccountConfigService;
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
 * RPA账号配置
 *
 * @author LL
 * @date 2026-01-28
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/accountConfig")
public class RpaAccountConfigController extends BaseController {

    private final IRpaAccountConfigService rpaAccountConfigService;

    /**
     * 查询RPA账号配置列表
     */
    @SaCheckPermission("rp:accountConfig:list")
    @GetMapping("/list")
    public TableDataInfo<RpaAccountConfigVo> list(RpaAccountConfigBo searchVO, PageQuery pageQuery) {
        return rpaAccountConfigService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出RPA账号配置列表
     */
    @SaCheckPermission("rp:accountConfig:export")
    @Log(title = "RPA账号配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(RpaAccountConfigBo rpaAccountConfig, HttpServletResponse response) {
        List<RpaAccountConfigVo> list = rpaAccountConfigService.queryList(rpaAccountConfig);
        ExcelUtil.exportExcel(list, "RPA账号配置", RpaAccountConfigVo.class, response);
    }

    /**
     * 获取RPA账号配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:accountConfig:query")
    @GetMapping("/{id}")
    public R<RpaAccountConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(rpaAccountConfigService.queryById(id));
    }

    /**
     * 新增RPA账号配置
     */
    @SaCheckPermission("rp:accountConfig:add")
    @Log(title = "RPA账号配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody RpaAccountConfigBo bo) {
        return toAjax(rpaAccountConfigService.insertRpaAccountConfig(bo));
    }

    /**
     * 修改RPA账号配置
     */
    @SaCheckPermission("rp:accountConfig:edit")
    @Log(title = "RPA账号配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody RpaAccountConfigBo bo) {
        return toAjax(rpaAccountConfigService.updateRpaAccountConfig(bo));
    }

    /**
     * 删除RPA账号配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:accountConfig:remove")
    @Log(title = "RPA账号配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(rpaAccountConfigService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "RPA账号配置数据", RpaAccountConfigVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:accountConfig:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<RpaAccountConfigVo> dataList = ExcelUtil.importExcel(file.getInputStream(), RpaAccountConfigVo.class);
        String operatorName = getUsername();
        String message = rpaAccountConfigService.importRpaAccountConfig(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
