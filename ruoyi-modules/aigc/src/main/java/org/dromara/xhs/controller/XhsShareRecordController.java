package org.dromara.xhs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.xhs.domain.vo.share.ShareCreateReqVO;
import org.dromara.xhs.domain.vo.share.ShareCreateRespVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.xhs.domain.vo.share.XhsShareRecordVo;
import org.dromara.xhs.domain.bo.XhsShareRecordBo;
import org.dromara.xhs.service.IXhsShareRecordService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 分享记录
 *
 * @author ZRL
 * @date 2025-07-24
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xhs/share")
public class XhsShareRecordController extends BaseController {

    private final IXhsShareRecordService xhsShareRecordService;


    /**
     * 创建分享内容
     * 支持图文和视频两种类型的分享创建
     *
     * @param createReqVO 分享内容信息
     * @return 分享结果，包含分享ID和分享链接
     */
    @SaCheckPermission("xhs:share:create")
    @Log(title = "创建分享", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/create")
    public R<ShareCreateRespVO> createShare(@Validated @RequestBody ShareCreateReqVO createReqVO) {
        return R.ok(xhsShareRecordService.createShare(createReqVO));
    }

    /**
     * 根据分享ID获取分享内容详情
     *
     * @param shareId 分享唯一标识符
     * @return 分享内容详情
     */
    @SaIgnore
    @GetMapping("/detail/{shareId}")
    public R<XhsShareRecordVo> getShareDetail(@PathVariable String shareId) {
        return R.ok(xhsShareRecordService.getShareDetail(shareId));
    }

}
