package com.codeleopard.servlet;

import com.codeleopard.pojo.User;
import com.codeleopard.service.user.UserService;
import com.codeleopard.service.user.UserServiceImpl;
import com.codeleopard.util.Constances;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取用户名和密码（根据前端input标签上的name来写）
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);

        // 看看能不能把人查出来
        if(user != null){
            req.getSession().setAttribute(Constances.USER_SESSION, user);
            resp.sendRedirect("jsp/frame.jsp");
        }else{
            req.setAttribute("error", "用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
