package com.iscas.lndicatormonitor.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class RSAUtil {

    /**
     * RSA 私钥，用于解密
     * 注意：这与前端的公钥是一对，需要通过标准方式生成
     */
    private static final String PRIVATE_KEY = 
        "-----BEGIN PRIVATE KEY-----\n" +
        "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALxpBltRuY5Cds+J\n" +
        "fUxCIwVHEBmsQresXPnUEg1xA11ekwJILbRA8qQUciK04WzFuDG8mlHFQ5RxNgMF\n" +
        "juh4VEuczH0yPh0ITlv1GnWMgonMksQmdjpyS2T9BQ/5NY7gT8nCtwdlKJCrst2C\n" +
        "kt0KPV1j/PjbBHQ4xUYopBsaetQlAgMBAAECgYA42Cf9DTmEZoX6GRNJmG0Bx1AK\n" +
        "C0Bhx9g3Qc9+fFJqQHO+FYrGur8S/QRILnx0dUH2HkClXGgXJKFfCRNFf/arNK83\n" +
        "63olcXeZYlMbKEp6HIvHLwjBVP8FT6GmAQH/1LBTZyRh8sKAx7k+2+ncDBFAk8tG\n" +
        "YKcMXaMzJrC3TYKUgQJBAOZ6ZEE8X9I2F3UVoD6fSD/mzwW3OXhzBxYmosNJVI9H\n" +
        "BxbgIzNQrYEb63DkBkMDwcTfLFBQKsH/lUElnVCLIJECQQDRL7U+XRNLh7/xMJfn\n" +
        "GBVbl5mViSADrIQFBU7yEZPVQDmXLGH3Ph9x9Lf7dVrLrKBWZ9jnVKl4NZT5JCvk\n" +
        "LUElAkANiuf5/jLcLpNiRPaHZK4sBYr5j/MxgdKGTL1zYXOQlF+zw0NkfS9UgVM7\n" +
        "hfGzfcezhNsYXfFZBNjAZYRudBbhAkAj1QsKTNAUALORwmcBZIWKEKB0QL5jfGSZ\n" +
        "0tNIWONQ1PZwNQnJYGTO+hx5T0QEKwrQgXKLZfh9SnOrk9jqSUGtAkAwxdLJGKEU\n" +
        "XjV9jNwQlryuWOk5JzE8XL4M6BQ+zH5/4dzIG2NUCvpgF1Rnpv4VQRCS0LCyBMsA\n" +
        "i01ZRt0fOyDm\n" +
        "-----END PRIVATE KEY-----";

    /**
     * 使用私钥解密
     * @param encryptedData 加密的数据
     * @return 解密后的数据
     */
    public static String decrypt(String encryptedData) {
        try {
            // 去除头尾标记和换行符
            String privateKeyPEM = PRIVATE_KEY
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            // Base64解码私钥
            byte[] keyBytes = Base64.decodeBase64(privateKeyPEM);
            
            // 使用PKCS8编码规范创建私钥规格
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            
            // 获取RSA密钥工厂
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            
            // 生成私钥
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            
            // 创建Cipher对象并初始化为解密模式
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            // 对加密数据进行Base64解码
            byte[] encryptedBytes = Base64.decodeBase64(encryptedData);
            
            // 解密数据
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            // 返回解密后的字符串
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}