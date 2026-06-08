package org.xlqs.com.example.util;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/6 下午10:25
 * @description JwtSecretGenerator 类说明：TODO
 * @since 2026/6/6
 */
public class JwtSecretGenerator {

    private static final String PREFIX = "xlqs-";

    public static String generate() {
        // 1. 使用安全随机数
        SecureRandom secureRandom = new SecureRandom();

        // 2. 生成 32 bytes 随机密钥（256位，足够安全）
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);

        // 3. Base64 URL安全编码
        String randomPart = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(keyBytes);

        // 4. 拼接前缀
        String secret = PREFIX + randomPart;

        // 5. 输出结果
        System.out.println("========== JWT SECRET ==========");
        System.out.println(secret);
        return secret;
    }
}
