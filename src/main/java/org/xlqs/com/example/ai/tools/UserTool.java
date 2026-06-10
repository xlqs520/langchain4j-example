package org.xlqs.com.example.ai.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xlqs.com.example.mapper.UserDao;

/**
 * @author xlqs
 * @version 1.0
 * @date 2026/6/10 下午11:15
 * @description UserTool 类说明：TODO
 * @since 2026/6/10
 */

@Service
@RequiredArgsConstructor
public class UserTool {

    public final UserDao userDao;

    @Tool("根据id查询用户")
    public String findById(
            @P("用户id") Long id
    ){
        return userDao.findById(id).toString();
    }
}
