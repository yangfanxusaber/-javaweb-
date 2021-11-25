package com.codeleopard.servlet;

import com.alibaba.fastjson.JSONArray;
import com.codeleopard.pojo.Role;
import com.codeleopard.pojo.User;
import com.codeleopard.service.role.RoleService;
import com.codeleopard.service.role.RoleServiceImpl;
import com.codeleopard.service.user.UserServiceImpl;
import com.codeleopard.util.Constances;
import com.codeleopard.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if(method.equals("savepwd") && method != null){
            this.updatePwd(req, resp);
        }else if(method.equals("pwdmodify") && method != null){
            this.pwdModify(req, resp);
        }else if(method.equals("query") && method != null){
            this.query(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Object attribute = req.getSession().getAttribute(Constances.USER_SESSION);

        String newpassword = req.getParameter("newpassword");
        boolean flag = false;

        System.out.println("newpassword: " + newpassword);

        if(attribute != null && !StringUtils.isNullOrEmpty(newpassword)){
            UserServiceImpl userService = new UserServiceImpl();
            try {
                flag = userService.updatePwd(((User)attribute).getId(), newpassword);
                if(flag){
                    req.setAttribute("message", "修改密码成功，请退出，使用新密码登录！");
                    req.getSession().removeAttribute(Constances.USER_SESSION);
                }else{
                    req.setAttribute("message", "修改密码失败");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            req.setAttribute("message", "新密码有问题");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    // 验证旧密码，session中有用户密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        // 从Session中拿到ID
        Object o = req.getSession().getAttribute(Constances.USER_SESSION);
        // 与input标签中的name属性（自己填的密码）
        String oldPassword = req.getParameter("oldpassword");

        // 万能的Map: 结果集
        Map<String, String> resultMap = new HashMap<String, String>();
        if(o == null){  // Session失效了，Session过期了
            resultMap.put("result", "sessionerror");    // 此处对应的是pwdmodify.js中data.result的值
        }else if(StringUtils.isNullOrEmpty(oldPassword)){   // 输入的密码为空
            resultMap.put("result", "error");
        }else{
            // 得到登录时候的密码
            String userPassword = ((User) o).getUserPassword();
            if(oldPassword.equals(userPassword)){
                resultMap.put("result", "true");
            }else{
                resultMap.put("result", "false");
            }
        }

        try {
            resp.setContentType("application/json");
//            resp.setContentType("text/html");
//            resp.setContentType("text/javascript");
            PrintWriter writer = resp.getWriter();
            // JSONArray 阿里巴巴的工具类，转换格式。
            // 返回到前端的是data.result
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 重点 + 难点
    // 连表查询
    public void query(HttpServletRequest req, HttpServletResponse resp){
        // 查询用户列表
        // 从前端获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        // 获取用户列表
        UserServiceImpl userService = new UserServiceImpl();

        // 第一次走这个请求，一定是第一页，页面大小固定的。
        int pageSize = 5;
        int currentPageNo = 1;


        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp); // 给查询赋值！
        }
        if(pageIndex != null){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        // 获取用户的总数（分页：上一页、下一页的情况）
        int userCount = userService.getUserCount(queryUserName, queryUserRole);
        // 总页数支持
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(userCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        System.out.println();
        // 控制首页和尾页
        if(totalPageCount < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        // 获取用户列表展示
        List<User> userList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", userList);

        RoleService roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        req.setAttribute("roleList", roleList);
        req.setAttribute("totalCount", userCount);
        req.setAttribute("currentPageNo", currentPageNo);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        System.out.println("totalPageCount: " + totalPageCount);
        // 返回前端
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
