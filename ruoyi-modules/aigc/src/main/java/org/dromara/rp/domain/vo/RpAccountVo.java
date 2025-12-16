package org.dromara.rp.domain.vo;

import org.dromara.rp.domain.RpAccount;
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
 * 账号信息视图对象 rp_account
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpAccount.class)
public class RpAccountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号ID
     */
    @ExcelProperty(value = "账号ID")
    private Long id;

    /**
     * 账号名称
     */
    @ExcelProperty(value = "账号名称")
    private String accountName;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号")
    private String deviceCode;

    /**
     * 账号平台（冗余字段，用于快速筛选）
     */
    @ExcelProperty(value = "账号平台", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "冗=余字段，用于快速筛选")
    private String platform;

    /**
     * 所属分组ID
     */
    @ExcelProperty(value = "所属分组ID")
    private Long groupId;


    @ExcelProperty(value = "所属分组名称")
    private String groupName;

    /**
     * 账号状态（1启用 0禁用）
     */
    @ExcelProperty(value = "账号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=启用,0=禁用")
    private Long status;

    /**
     * 额外信息
     */
    @ExcelProperty(value = "额外信息")
    private String extraInfo;


}
