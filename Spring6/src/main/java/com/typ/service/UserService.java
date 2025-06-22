package com.typ.service;

import com.typ.pojo.User;

public interface UserService {
    public abstract User selectOneUser(String uname,String pwd);
}
