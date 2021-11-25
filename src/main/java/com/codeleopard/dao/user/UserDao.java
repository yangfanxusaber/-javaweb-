package com.codeleopard.dao.user;

import com.codeleopard.pojo.Role;
import com.codeleopard.pojo.User;

import java.sql.Connection;
import java.util.List;

public interface UserDao {
    //  得到要登录的用户
    public User getLoginUser(Connection connection, String userCode, String userPassword);

    // 修改密码
    public int updatePwd(Connection connection, int id, String userPassword) throws Exception;

    // 根据用户名或者角色查询用户总数
    public int getUserCount(Connection connection, String userName, int userRole) throws Exception;

    // 获取用户列表   userlist
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPage, int pageSize) throws Exception;

}
