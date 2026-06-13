package org.xlqs.com.example.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/7 下午7:09
 * @description StartupRunner 类说明：TODO
 * @since 2026/6/7
 */
@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Value("${GREETING}")
    private String greeting;

    @Value("${ssl.password}")
    private String sslPassword;

    @Override
    public void run(String... args) throws Exception {
        log.info("------------------------------------------");
        log.info(greeting);
        log.info("------------------------------------------");
        log.info("SSL 密码: {}", sslPassword);
    }
}