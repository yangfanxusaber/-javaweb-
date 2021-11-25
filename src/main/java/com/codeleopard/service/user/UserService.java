package com.codeleopard.service.user;

import com.codeleopard.pojo.User;

import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String userPassword);

    // 修改密码
    public boolean updatePwd(int id, String userPassword) throws Exception;

    // 查询记录数
    public int getUserCount(String userName, int userRole);

    // 根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

}
