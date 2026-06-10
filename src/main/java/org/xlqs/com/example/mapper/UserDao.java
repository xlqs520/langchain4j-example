package org.xlqs.com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xlqs.com.example.dto.UserDto;

/**
* @author huawei
* @description 针对表【user(用户列表)】的数据库操作Mapper
* @createDate 2026-06-10 22:46:52
* @Entity org.xlqs.com.example.dto.UserDto
*/

@Mapper
public interface UserDao extends BaseMapper<UserDto> {

    UserDto findById(Long id);
}




