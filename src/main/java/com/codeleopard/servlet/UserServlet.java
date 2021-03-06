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
                    req.setAttribute("message", "?????????????????????????????????????????????????????????");
                    req.getSession().removeAttribute(Constances.USER_SESSION);
                }else{
                    req.setAttribute("message", "??????????????????");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            req.setAttribute("message", "??????????????????");
        }
        req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
    }

    // ??????????????????session??????????????????
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        // ???Session?????????ID
        Object o = req.getSession().getAttribute(Constances.USER_SESSION);
        // ???input????????????name??????????????????????????????
        String oldPassword = req.getParameter("oldpassword");

        // ?????????Map: ?????????
        Map<String, String> resultMap = new HashMap<String, String>();
        if(o == null){  // Session????????????Session?????????
            resultMap.put("result", "sessionerror");    // ??????????????????pwdmodify.js???data.result??????
        }else if(StringUtils.isNullOrEmpty(oldPassword)){   // ?????????????????????
            resultMap.put("result", "error");
        }else{
            // ???????????????????????????
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
            // JSONArray ??????????????????????????????????????????
            // ?????????????????????data.result
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ?????? + ??????
    // ????????????
    public void query(HttpServletRequest req, HttpServletResponse resp){
        // ??????????????????
        // ?????????????????????
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;

        // ??????????????????
        UserServiceImpl userService = new UserServiceImpl();

        // ????????????????????????????????????????????????????????????????????????
        int pageSize = 5;
        int currentPageNo = 1;


        if(queryUserName == null){
            queryUserName = "";
        }
        if(temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp); // ??????????????????
        }
        if(pageIndex != null){
            currentPageNo = Integer.parseInt(pageIndex);
        }

        // ??????????????????????????????????????????????????????????????????
        int userCount = userService.getUserCount(queryUserName, queryUserRole);
        // ???????????????
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(userCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        System.out.println();
        // ?????????????????????
        if(totalPageCount < 1){
            currentPageNo = 1;
        }else if(currentPageNo > totalPageCount){
            currentPageNo = totalPageCount;
        }

        // ????????????????????????
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
        // ????????????
        try {
            req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
