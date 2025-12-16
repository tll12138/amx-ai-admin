package org.dromara.xhs.util;

import java.security.SecureRandom;

public class StringGeneratorUtil {

    // 默认字符集（URL-safe）
    private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    // 安全的随机源（线程安全）
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String RED_PREFIX = "red_";

    /**
     * 生成默认长度（21位）的 NanoID
     */
    public static String generate() {
        return NanoIdUtils.randomNanoId();
    }

    /**
     * 生成指定长度的 NanoID（使用默认字符集）
     * @param length 分享ID的长度
     */
    public static String generate(int length) {
        return NanoIdUtils.randomNanoId(SECURE_RANDOM, DEFAULT_ALPHABET, length);
    }

    /**
     * 自定义字符集 + 长度
     * @param alphabet 自定义字符集（如只用数字/大写字母等）
     * @param length ID长度
     */
    public static String generate(char[] alphabet, int length) {
        return NanoIdUtils.randomNanoId(SECURE_RANDOM, alphabet, length);
    }

    /**
     * 生成默认长度（12位）的 红薯分享ID
     */
    public static String generateShareId() {
        return redShareIdGenerate(12);
    }

    /**
     * 随机生成指定长度的红书分享ID
     * @param length 分享ID的长度
     */
    public static String redShareIdGenerate(int length) {
        return RED_PREFIX + NanoIdUtils.randomNanoId(SECURE_RANDOM, DEFAULT_ALPHABET, length);
    }
}
