package org.dromara.ai.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.xhs.util.XHSUtils;
import org.dromara.xhs.util.XhsReqVo;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zrl
 * @date 2025/3/26
 */

@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/ai/util")
public class UtilController {

    @PostMapping("/parseXhs")
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
