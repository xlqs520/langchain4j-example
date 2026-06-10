package org.xlqs.com.example;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xlqs.com.example.mapper.UserDao;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/10 下午10:56
 * @description SQLTest 类说明：TODO
 * @since 2026/6/10
 */

@SpringBootTest
public class SQLTest {

    @Resource
    private UserDao userDao;

    @Test
    void testUserMapper() {
        System.out.println(userDao.findById(1L));
    }

}
