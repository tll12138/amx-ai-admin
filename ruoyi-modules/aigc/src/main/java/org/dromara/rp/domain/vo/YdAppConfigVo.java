package org.dromara.rp.domain.vo;

import org.dromara.rp.domain.YdAppConfig;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 影刀应用配置视图对象 yd_app_config
 *
 * @author LL
 * @date 2026-03-12
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = YdAppConfig.class)
public class YdAppConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 平台
     */
    @ExcelProperty(value = "平台", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "send_platform")
    private String platform;

    /**
     * 设备类型
     */
    @ExcelProperty(value = "设备类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "device_type")
    private String deviceType;

    /**
     * 应用id
     */
    @ExcelProperty(value = "应用id")
    private String applicationId;

    /**
     * 状态 (0正常 1异常)
     */
    @ExcelProperty(value = "状态 (0正常 1异常)")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
