package org.dromara.ai.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.dromara.ai.domain.SpuInfo;
import org.dromara.ai.service.ISpuInfoService;
import org.dromara.ai.service.IXhsCookieService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品信息Service业务层处理
 *
 * @author ZRL
 * @date 2025-03-24
 */
@RequiredArgsConstructor

@Service
public class SpuInfoServiceImpl implements ISpuInfoService {

    @Override
    @DS("puqi")
    public List<SpuInfo> querySpuList() {
        return Db.lambdaQuery(SpuInfo.class)
            .isNotNull(SpuInfo::getSpu)
            .list();
    }
}
