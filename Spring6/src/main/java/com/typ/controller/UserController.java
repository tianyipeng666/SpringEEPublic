package com.typ.controller;

import com.typ.pojo.User;
import com.typ.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/upload3")
    @ResponseBody
    public Map upload3(MultipartFile photo, HttpServletRequest req) throws IOException {
        // 把文件放在服务器目录：
        String realPath = req.getServletContext().getRealPath("/images");
        System.out.println(realPath);

        File dir = new File(realPath);
        if (!dir.exists()) {// 如果目录不存在，创建目录
            dir.mkdirs();
        }

        // 获取上传图片的原始名字
        String originalFilename = photo.getOriginalFilename();
        // 获取文件后缀：
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        // 创建文件唯一名字：
        // 方式1：利用时间戳+随机数+后缀：
        /*long time01 = System.currentTimeMillis();
        String filename = time01 + "" + new Random().nextInt(1000) + suffix;*/

        // 方式2：UUID
        UUID uuid = UUID.randomUUID();
        String filename = uuid + suffix;

        File file = new File(dir, filename);
        // 文件保存：
        photo.transferTo(file);

        // 把图片名字保存到集合中，返回给前端：
        Map map = new HashMap();
        map.put("msg", 1);
        map.put("filename", filename);
        map.put("filetype", photo.getContentType());
        return map;
    }

    @RequestMapping("/login")
    public String login(User user) {
        System.out.println("调用controller层");
        System.out.println(user);
        int n = userService.saveUser(user);
        if (n > 0) {// 添加成功
            return "success.jsp";
        }

        return "fail.jsp";// 如果没有添加成功，就跳转到失败页面
    }


    @RequestMapping("/showusers")
    @ResponseBody
    public List<User> showusers(User user) {
        List<User> users = userService.findAllUsers();
        return users;
    }


    @RequestMapping("/download")
    public void download(HttpServletRequest req, HttpServletResponse resp, String filename, String filetype) throws IOException {
        // 想要在浏览器中展示下载效果，下载到本地盘符：
        // 设置响应头和响应类型：
        resp.setHeader("Content-Disposition", "attachment;filename" + filename);
        resp.setContentType(filetype);
        // 【1】将图片从服务器读取到程序：
        String realPath = req.getServletContext().getRealPath("/images");
        InputStream is = new FileInputStream(realPath + "/" + filename);
        // 【2】将程序中的图片写入到本地：
        //OutputStream os = new FileOutputStream("d:/1.png");
        ServletOutputStream os = resp.getOutputStream();

        // 边读进来 边 写出去：
        int n = is.read();
        while (n != -1) {
            os.write(n);
            n = is.read();
        }


        // 关闭流：
        os.close();
        is.close();


    }


}
