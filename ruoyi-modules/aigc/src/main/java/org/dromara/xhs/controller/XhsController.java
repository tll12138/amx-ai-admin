package org.dromara.xhs.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import org.dromara.common.core.domain.R;
import org.dromara.xhs.domain.XhsTokenResponseData;
import org.dromara.xhs.domain.vo.signature.XhsSignatureRequest;
import org.dromara.xhs.domain.vo.signature.XhsSignatureResponse;
import org.dromara.xhs.domain.vo.signature.XhsTokenRequest;
import org.dromara.xhs.service.XhsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 小红书API控制器
 */
@RestController
@RequestMapping("/xhs")
@CrossOrigin(origins = "*") // 根据实际需要配置CORS
public class XhsController {

    @Autowired
    private XhsService xhsService;

    /**
     * 获取access_token
     */
    @PostMapping("/auth/token")
    public R<XhsTokenResponseData> getAccessToken(@RequestBody XhsTokenRequest request) {
        try {
            XhsTokenResponseData response = xhsService.getAccessToken(request);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail();
        }
    }

    /**
     * 获取分享签名
     */
    @SaIgnore
    @PostMapping("/share/signature")
    public R<XhsSignatureResponse> getShareSignature(@RequestBody XhsSignatureRequest request) {
        try {
            XhsSignatureResponse response = xhsService.getShareSignature(request);
            return R.ok(response);
        } catch (Exception e) {
            return R.fail();
        }
    }

    /**
     * 刷新红书Token
     *
     * @return 分享内容详情
     */
    @SaIgnore
    @GetMapping("/auth/refresh")
    public R refreshToken() {
        xhsService.refreshToken();
        return R.ok();
    }

}
