package com.typ.service;

import com.typ.pojo.User;

import java.util.List;

public interface UserService {
    User selectOneUser(String uname,String pwd);

    int saveUser(User user);

    List<User> findAllUsers();
}
