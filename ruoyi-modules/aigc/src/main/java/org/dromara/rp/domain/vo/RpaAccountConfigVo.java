package org.dromara.rp.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.rp.domain.RpaAccountConfig;

import java.io.Serial;
import java.io.Serializable;



/**
 * RPA账号配置视图对象 rpa_account_config
 *
 * @author LL
 * @date 2026-01-28
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpaAccountConfig.class)
public class RpaAccountConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Long id;

    /**
     * 账号名称
     */
    @ExcelProperty(value = "账号名称")
    private String robotClientName;

    /**
     * 账号唯一标识
     */
    @ExcelProperty(value = "账号唯一标识")
    private String robotClientUuid;

    /**
     * 状态 (0正常 1异常)
     */
    @ExcelProperty(value = "状态 (0正常 1异常)", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_notice_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
