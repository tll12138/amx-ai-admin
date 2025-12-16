package org.dromara.common.core.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncodeAndDecodeUtils {

    private static final  String PWD = "puqi";

    public EncodeAndDecodeUtils() {
    }

    public static String encode(String var1) {
        Object var2 = null;

        byte[] var11;
        try {
            KeyGenerator var3 = KeyGenerator.getInstance("AES");
            SecureRandom var4 = SecureRandom.getInstance("SHA1PRNG");
            var4.setSeed(PWD.getBytes());
            var3.init(128, var4);
            SecretKey var5 = var3.generateKey();
            byte[] var6 = var5.getEncoded();
            SecretKeySpec var7 = new SecretKeySpec(var6, "AES");
            Cipher var8 = Cipher.getInstance("AES/ECB/PKCS5Padding");
            var8.init(1, var7);
            byte[] var9 = var1.getBytes();
            var11 = var8.doFinal(var9);
        } catch (Exception var10) {
            System.out.println("加密异常！" + var10);
            return null;
        }
        return parseByte2HexStr(var11);
    }

    public static String decode(String var1) {
        Object var2 = null;
        byte[] var3 = parseHexStr2Byte(var1);

        byte[] var11;
        try {
            KeyGenerator var4 = KeyGenerator.getInstance("AES");
            SecureRandom var5 = SecureRandom.getInstance("SHA1PRNG");
            var5.setSeed(PWD.getBytes());
            var4.init(128, var5);
            SecretKey var6 = var4.generateKey();
            byte[] var7 = var6.getEncoded();
            SecretKeySpec var8 = new SecretKeySpec(var7, "AES");
            Cipher var9 = Cipher.getInstance("AES/ECB/PKCS5Padding");
            var9.init(2, var8);
            var11 = var9.doFinal(var3);
        } catch (Exception var10) {
            System.out.println("解密异常！" + var10);
            return null;
        }

        return new String(var11);
    }


    private static String parseByte2HexStr(byte[] var1) {
        StringBuilder var2 = new StringBuilder();

        for (byte b : var1) {
            String var4 = Integer.toHexString(b & 255);
            if (var4.length() == 1) {
                var4 = '0' + var4;
            }

            var2.append(var4.toUpperCase());
        }

        return var2.toString();
    }

    private static byte[] parseHexStr2Byte(String var1) {
        if (var1 == null || var1.isEmpty()) {
            return null;
        } else {
            byte[] var2 = new byte[var1.length() / 2];

            for (int var3 = 0; var3 < var1.length() / 2; ++var3) {
                int var4 = Integer.parseInt(var1.substring(var3 * 2, var3 * 2 + 1), 16);
                int var5 = Integer.parseInt(var1.substring(var3 * 2 + 1, var3 * 2 + 2), 16);
                var2[var3] = (byte) (var4 * 16 + var5);
            }

            return var2;
        }
    }

}
