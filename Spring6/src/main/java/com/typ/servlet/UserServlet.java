package com.typ.servlet;

import com.typ.pojo.User;
import com.typ.service.UserService;
import com.typ.service.impl.UserServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/loginServlet")
public class UserServlet extends HttpServlet {
    private UserService us;

    @Override
    public void init() throws ServletException {
        ApplicationContext ac = new ClassPathXmlApplicationContext("application.xml");
        us = (UserServiceImpl)ac.getBean("usi");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理乱码：
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        // 接收前台参数：
        String uname = req.getParameter("uname");
        String pwd = req.getParameter("pwd");
        System.out.println(uname);
        System.out.println(pwd);
        // 调用service层
        User user = us.selectOneUser(uname, pwd);
        // 对user判断：
        if (user != null){// 数据查询到了
            // 跳转到首页：
            req.getRequestDispatcher("index.jsp").forward(req,resp);
        }else{
            resp.getWriter().write("登录失败！");
        }
    }
}
