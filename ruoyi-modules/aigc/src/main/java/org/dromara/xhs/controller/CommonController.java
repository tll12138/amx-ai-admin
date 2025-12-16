package org.dromara.xhs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.ai.base.service.AIContentServiceFacade;
import org.dromara.common.core.domain.R;
import org.dromara.common.encrypt.annotation.ApiEncrypt;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.dromara.xhs.domain.bo.XhsShareNoteBo;
import org.dromara.xhs.domain.vo.common.TransformContentReqVO;
import org.dromara.xhs.domain.vo.common.TransformContentRespVO;
import org.dromara.xhs.domain.vo.common.TransformImageReqVO;
import org.dromara.xhs.service.CommonService;
import org.dromara.xhs.util.XHSUtils;
import org.dromara.xhs.util.XhsReqVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zrl
 * @date 2025/7/30
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/xhs/common")
public class CommonController extends BaseController {

    private final CommonService commonService;

    @PostMapping("/transform/images")
    @Log(title = "图片加滤镜", businessType = BusinessType.UPDATE)
    public R<List<String>> transformImages(@RequestBody TransformImageReqVO reqVO) {
        return R.ok(commonService.transformImages(reqVO));
    }

    @PostMapping("/generate/content")
    @Log(title = "内容优化", businessType = BusinessType.UPDATE)
    public R<TransformContentRespVO> generateContent(@RequestBody TransformContentReqVO reqVO) {
        return R.ok(commonService.generateContent(reqVO));
    }

    @SaIgnore
    @ApiEncrypt
    @PostMapping("/addNote")
    public R<Boolean> generateContent(@RequestBody XhsShareNoteBo shareNoteBo) {
        return R.ok(commonService.addNote(shareNoteBo));
    }

    @PostMapping("/parse")
    @Log(title = "小红书笔记链接解析", businessType = BusinessType.OTHER)
    public R<JSONObject> handleXhsUrl(@RequestBody XhsReqVo xhsReqVo) {
        if (xhsReqVo == null || StrUtil.isEmpty(xhsReqVo.getUrl())) {
            return R.fail("url不能为空");
        }
        try {
            JSONObject noteInfoByUrl = XHSUtils.getNoteInfoByUrl(xhsReqVo.getUrl());
            return R.ok(noteInfoByUrl);
        } catch (Exception e) {
            log.error("解析失败：{}", e.getMessage());
            return R.fail("解析失败，请重新尝试~");
        }
    }
}
