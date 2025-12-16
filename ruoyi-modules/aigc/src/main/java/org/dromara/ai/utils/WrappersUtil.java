//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.dromara.ai.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

public final class WrappersUtil {

    private WrappersUtil() {
    }

    public static <T> LambdaQueryWrapper<T> lambdaQuery() {
        return new LambdaQueryWrapperX<>();
    }
}
