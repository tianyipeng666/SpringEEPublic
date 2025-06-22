package com.typ.mapper;

import com.typ.pojo.User;

public interface UserMapper {
    public abstract User selectOneUser(String uname,String pwd);
}
