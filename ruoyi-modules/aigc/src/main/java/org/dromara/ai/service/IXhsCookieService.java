package org.dromara.ai.service;

import org.dromara.ai.domain.SpuInfo;
import org.dromara.ai.domain.XhsCookie;

import java.util.List;

/**
 * @author zrl
 * @date 2025/4/2
 */
public interface IXhsCookieService {

    List<XhsCookie> queryCookieList();
}
