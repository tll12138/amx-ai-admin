package org.dromara.xhs.domain.vo.share;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zrl
 * @date 2025/7/24
 */
@Data
@Builder
public class ShareCreateRespVO {

    private String shareId;
    private String shareUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
}


