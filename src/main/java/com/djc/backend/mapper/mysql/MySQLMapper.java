package com.djc.backend.mapper.mysql;

import com.djc.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MySQLMapper {
     User queryUserById(String id);
     List<User> queryAllUsers();
}
