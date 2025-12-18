package org.dromara.rp.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.rp.domain.RpAccountGroup;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 账号分组视图对象 rp_account_group
 *
 * @author ZRL
 * @date 2025-11-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RpAccountGroup.class)
public class RpAccountGroupVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    private Long id;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 分组所属平台（如: WeChat, Douyin, Weibo 等）
     */
    private String platform;

    /**
     * 额外信息
     */
    private String extraInfo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 关联rpa账号
     */
    private Integer rpa_no;
}
