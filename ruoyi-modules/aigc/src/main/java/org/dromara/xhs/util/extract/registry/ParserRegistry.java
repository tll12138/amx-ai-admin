package org.dromara.xhs.util.extract.registry;

import lombok.extern.slf4j.Slf4j;
import org.dromara.xhs.util.extract.core.Parser;
import org.dromara.xhs.util.extract.model.PlatformEnum;
import org.dromara.xhs.util.extract.parser.DouYinParser;
import org.dromara.xhs.util.extract.parser.XiaoHongShuParser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 解析器注册中心
 * 负责管理所有解析器实例，支持动态注册和获取
 *
 * @author ZRL
 */
@Slf4j
public class ParserRegistry {

    /**
     * 解析器实例缓存
     */
    private static final Map<PlatformEnum, Parser> PARSER_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析器类映射
     */
    private static final Map<PlatformEnum, Class<? extends Parser>> PARSER_CLASSES = new HashMap<>();

    static {
        // 注册默认解析器
        registerParser(PlatformEnum.XIAO_HONG_SHU, XiaoHongShuParser.class);
        registerParser(PlatformEnum.DOU_YIN, DouYinParser.class);
    }

    /**
     * 注册解析器类
     */
    public static void registerParser(PlatformEnum platformEnum, Class<? extends Parser> parserClass) {
        PARSER_CLASSES.put(platformEnum, parserClass);
        // 清除缓存，强制重新创建实例
        PARSER_CACHE.remove(platformEnum);
        log.info("注册解析器: {} -> {}", platformEnum, parserClass.getSimpleName());
    }

    /**
     * 注册解析器实例
     */
    public static void registerParser(PlatformEnum platformEnum, Parser parser) {
        PARSER_CACHE.put(platformEnum, parser);
        log.info("注册解析器实例: {} -> {}", platformEnum, parser.getClass().getSimpleName());
    }

    /**
     * 获取解析器实例
     */
    public static Parser getParser(PlatformEnum platformEnum) {
        return PARSER_CACHE.computeIfAbsent(platformEnum, type -> {
            Class<? extends Parser> parserClass = PARSER_CLASSES.get(type);
            if (parserClass == null) {
                log.warn("未找到平台 {} 对应的解析器", type);
                return null;
            }

            try {
                Parser parser = parserClass.getDeclaredConstructor().newInstance();
                log.debug("创建解析器实例: {} -> {}", type, parserClass.getSimpleName());
                return parser;
            } catch (Exception e) {
                log.error("创建解析器实例失败: {} -> {}", type, parserClass.getSimpleName(), e);
                return null;
            }
        });
    }

    /**
     * 获取所有已注册的解析器
     */
    public static List<Parser> getAllParsers() {
        List<Parser> parsers = new ArrayList<>();
        for (PlatformEnum platformEnum : PARSER_CLASSES.keySet()) {
            Parser parser = getParser(platformEnum);
            if (parser != null) {
                parsers.add(parser);
            }
        }
        return parsers;
    }

    /**
     * 获取支持的平台类型
     */
    public static Set<PlatformEnum> getSupportedPlatforms() {
        return new HashSet<>(PARSER_CLASSES.keySet());
    }

    /**
     * 检查是否支持指定平台
     */
    public static boolean isSupported(PlatformEnum platformEnum) {
        return PARSER_CLASSES.containsKey(platformEnum);
    }

    /**
     * 移除解析器
     */
    public static void removeParser(PlatformEnum platformEnum) {
        PARSER_CLASSES.remove(platformEnum);
        PARSER_CACHE.remove(platformEnum);
        log.info("移除解析器: {}", platformEnum);
    }

    /**
     * 清空所有解析器
     */
    public static void clear() {
        PARSER_CLASSES.clear();
        PARSER_CACHE.clear();
        log.info("清空所有解析器");
    }

    /**
     * 获取解析器统计信息
     */
    public static Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("registeredParsers", PARSER_CLASSES.size());
        stats.put("cachedInstances", PARSER_CACHE.size());
        stats.put("supportedPlatforms", getSupportedPlatforms());
        return stats;
    }
}
