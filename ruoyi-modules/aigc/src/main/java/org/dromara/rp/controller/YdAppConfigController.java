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
import org.dromara.rp.domain.vo.YdAppConfigVo;
import org.dromara.rp.domain.bo.YdAppConfigBo;
import org.dromara.rp.domain.YdAppConfig;
import org.dromara.rp.service.IYdAppConfigService;
import static org.dromara.common.satoken.utils.LoginHelper.getUsername;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 影刀应用配置
 *
 * @author LL
 * @date 2026-03-12
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/rp/appConfig")
public class YdAppConfigController extends BaseController {

    private final IYdAppConfigService ydAppConfigService;

    /**
     * 查询影刀应用配置列表
     */
    @SaCheckPermission("rp:appConfig:list")
    @GetMapping("/list")
    public TableDataInfo<YdAppConfigVo> list(YdAppConfigBo searchVO, PageQuery pageQuery) {
        return ydAppConfigService.queryPageList(searchVO, pageQuery);
    }

    /**
     * 导出影刀应用配置列表
     */
    @SaCheckPermission("rp:appConfig:export")
    @Log(title = "影刀应用配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(YdAppConfigBo ydAppConfig, HttpServletResponse response) {
        List<YdAppConfigVo> list = ydAppConfigService.queryList(ydAppConfig);
        ExcelUtil.exportExcel(list, "影刀应用配置", YdAppConfigVo.class, response);
    }

    /**
     * 获取影刀应用配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("rp:appConfig:query")
    @GetMapping("/{id}")
    public R<YdAppConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(ydAppConfigService.queryById(id));
    }

    /**
     * 新增影刀应用配置
     */
    @SaCheckPermission("rp:appConfig:add")
    @Log(title = "影刀应用配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody YdAppConfigBo bo) {
        return toAjax(ydAppConfigService.insertYdAppConfig(bo));
    }

    /**
     * 修改影刀应用配置
     */
    @SaCheckPermission("rp:appConfig:edit")
    @Log(title = "影刀应用配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody YdAppConfigBo bo) {
        return toAjax(ydAppConfigService.updateYdAppConfig(bo));
    }

    /**
     * 删除影刀应用配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("rp:appConfig:remove")
    @Log(title = "影刀应用配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(ydAppConfigService.deleteWithValidByIds(List.of(ids), true));
    }


    /**
     * 导入模板下载
     * @param response 响应对象
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil.exportExcel(new ArrayList<>(), "影刀应用配置数据", YdAppConfigVo.class, response);
    }


    /**
     * 批量导入
     * @param file 响应对象
     * @param updateSupport 是否更新已存在数据
     */
    @SaCheckPermission("rp:appConfig:add")
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        List<YdAppConfigVo> dataList = ExcelUtil.importExcel(file.getInputStream(), YdAppConfigVo.class);
        String operatorName = getUsername();
        String message = ydAppConfigService.importYdAppConfig(dataList, updateSupport, operatorName);
        return R.ok(message);
    }
}
