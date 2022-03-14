package com.djc.backend.mapper.mysql;

import com.djc.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface MySQLMapper {
     User queryUserById(String id);
     List<User> queryAllUsers();
     Integer getUsersNum();
     Map<String, Object> getUsersAVG();
}
