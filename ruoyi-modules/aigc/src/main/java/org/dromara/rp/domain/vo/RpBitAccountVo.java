package org.dromara.rp.domain.vo;

import org.dromara.rp.domain.RpBitAccount;
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
 * 比特账号信息视图对象 rp_bit_account
 *
 * @author LL
 * @date 2026-03-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpBitAccount.class)
public class RpBitAccountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 比特账号ID
     */
    @ExcelProperty(value = "比特账号ID")
    private Long id;

    /**
     * 账号名称
     */
    @ExcelProperty(value = "账号名称")
    private String accountName;

    /**
     * 账号编码
     */
    @ExcelProperty(value = "账号编码")
    private String accountCode;

    /**
     * 账号状态
     */
    @ExcelProperty(value = "账号状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_enable")
    private Long status;


}
