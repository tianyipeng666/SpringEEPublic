package com.typ.mapper;

import com.typ.pojo.User;

import java.util.List;

public interface UserMapper {
    User selectOneUser(String uname, String pwd);

    int insertUser(User user);

    List<User> selectAllUsers();
}
