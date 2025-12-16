package org.dromara.xhs.domain.vo.share;

import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.ai.domain.map.ConvertStrAndList;
import org.dromara.xhs.domain.XhsShareRecord;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * @author zrl
 * @date 2025/7/24
 */
@Data
@AutoMapper(target = XhsShareRecord.class, reverseConvertGenerate = false, uses = ConvertStrAndList.class)
public class ShareCreateReqVO {

    private String title;

    @NotNull(message = "内容不能为空")
    private String content;

    @NotNull(message = "分享类型不能为空")
    private String type;

    /**
     * 图片列表
     */
    @AutoMapping(target = "images", qualifiedByName = "List2Str")
    private List<String> images;

    /**
     * 视频
     */
    private String video;

    /**
     * 视频封面
     */
    private String cover;

    /**
     * 用户ID
     */
    private Long userId;

    public ShareCreateReqVO() {}
}
