package com.typ.service.impl;

import com.typ.mapper.UserMapper;
import com.typ.pojo.User;
import com.typ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Component注解的作用：简化了applicationContext.xml中对这个创建对象的配置 ，而创建对象这件事还是spring来管理。
 * 帮我们构建对象，默认的名字就是类名的首字母小写： UserServiceImpl  ---》 userServiceImpl
 * 我们也可以指定对象的名字：通过传入参数的形式：@Component("usi")
 *      @Repository：@Component子标签。作用和@Component一样。用在持久层
 *      @Service：@Component子标签。作用和@Component一样。用在业务层
 *      @Controller：@Component子标签。作用和@Component一样。用在控制器层
 *      @Configuration：@Component子标签。作用和@Component一样。用在配置类上，以后结合SpringBoot使用
 */
@Service("usi")
public class UserServiceImpl implements UserService {
    /**
    * 加入@Autowired注解的作用：帮我们在创建对象以后完成属性的注入，此时我们不需要提供setter方法
    * 注入形式：先按照类型从spring容器中去找匹配的对象注入进来。
    * 如果容器中存在多个相同类型的对象，那么就按照名字去找。
    * 比如容器中UserMapper实现类对象1 ：um1   还有UserMapper实现类对象2：um2
    * 此时就需要搭配另一个注解：@Qualifier("um1")  来指定你需要的那个对象
    * 使用了 @Qualifier("um1")注解后@Autowired是不能省略的，因为@Autowired帮我们完成属性注入，
    * @Qualifier只是定位到你需要注入的对象，必须搭配使用
    * */
    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectOneUser(String uname, String pwd) {
        return userMapper.selectOneUser(uname,pwd);
    }

}
