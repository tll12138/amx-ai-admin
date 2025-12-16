package org.dromara.xhs.util.extract.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持的域名注解
 * 用于声明解析器支持的域名列表
 *
 * @author ZRL
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedDomains {
    /**
     * 支持的域名列表
     * @return 域名数组
     */
    String[] value();

    /**
     * 是否启用严格模式
     * 严格模式下会检查协议和完整域名匹配
     * 非严格模式下只检查URL是否包含域名字符串
     * @return 默认false（非严格模式）
     */
    boolean strict() default false;
}
