package org.dromara.xhs.service;

import org.dromara.common.core.domain.R;
import org.dromara.xhs.domain.XhsShareRecord;
import org.dromara.xhs.domain.vo.share.XhsShareRecordVo;
import org.dromara.xhs.domain.bo.XhsShareRecordBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.xhs.domain.vo.share.ShareCreateReqVO;
import org.dromara.xhs.domain.vo.share.ShareCreateRespVO;

import java.util.List;
import java.util.Map;

/**
 * 分享记录Service接口
 *
 * @author ZRL
 * @date 2025-07-24
 */
public interface IXhsShareRecordService {

    /**
     * 创建分享
     *
     * @param createReqVO 分享信息
     * @return 创建结果，包含分享ID、分享链接等信息
     */
    ShareCreateRespVO createShare(ShareCreateReqVO createReqVO);


    /**
     * 根据分享ID查询分享记录
     *
     * @param shareId 分享ID
     * @return 分享记录
     */
    XhsShareRecord queryByShareId(String shareId);

    /**
     * 根据分享ID获取分享详情
     *
     * @param shareId 分享唯一标识符
     * @return 分享内容详情
     */
    XhsShareRecordVo getShareDetail(String shareId);

    void refreshToken();
}
