package org.dromara.xhs.domain.vo.share;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapping;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.xhs.domain.XhsShareRecord;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 分享记录视图对象 xhs_share_record
 *
 * @author ZRL
 * @date 2025-07-24
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = XhsShareRecord.class, uses = ConvertStrAndList.class)
public class XhsShareRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分享记录ID
     */
    @ExcelProperty(value = "分享记录ID")
    private Long id;

    /**
     * 分享唯一标识
     */
    @ExcelProperty(value = "分享唯一标识")
    private String shareId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 标题
     */
    @ExcelProperty(value = "标题")
    private String title;

    /**
     * 内容
     */
    @ExcelProperty(value = "内容")
    private String content;

    /**
     * 图片列表（图文）
     */
    @ExcelProperty(value = "图片列表")
    @AutoMapping(target = "images", qualifiedByName = "List2Str")
    private List<String> images;

    /**
     * 视频
     */
    @ExcelProperty(value = "视频")
    private String video;

    /**
     * 视频封面
     */
    @ExcelProperty(value = "视频封面")
    private String cover;

    /**
     * 分享类型：normal-图文，video-视频
     */
    @ExcelProperty(value = "分享类型")
    private String type;

    /**
     * 状态：0-已删除，1-正常，2-已过期
     */
    @ExcelProperty(value = "状态：0-已删除，1-正常，2-已过期")
    private Integer status;

    /**
     * 过期时间
     */
    @ExcelProperty(value = "过期时间")
    private LocalDateTime expiresAt;

//    /**
//     * 访问次数
//     */
//    @ExcelProperty(value = "访问次数")
//    private Integer accessCount;
//
//    /**
//     * 最近访问时间
//     */
//    @ExcelProperty(value = "最近访问时间")
//    private LocalDateTime accessTime;

    /**
     * 分享链接
     */
    private String shareUrl;
}
