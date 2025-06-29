package com.typ.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: zhaoss
 */
public class MyInterceptor implements HandlerInterceptor {
    /*@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle");
        // 进行页面维护，拦截进入到维护页面：
        response.sendRedirect("/demo03/wh.jsp");
        return false;
    }*/

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandle");
        /*// 对数据进行过滤：

        // 数据怎么取出？
        Map<String, Object> map = modelAndView.getModel();
        String s = (String)map.get("msg");
        // 敏感词汇输出***
        if (s.contains("TMD")){
            // 替换***
            String newstr = s.replaceAll("TMD", "***");
            map.put("msg",newstr);
        }*/

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion");
       /* System.out.println(ex);*/
    }
}
