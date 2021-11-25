package com.codeleopard.service.user;

import com.codeleopard.dao.BaseDao;
import com.codeleopard.dao.user.UserDao;
import com.codeleopard.dao.user.UserDaoImpl;
import com.codeleopard.pojo.User;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

public class UserServiceImpl implements UserService{

    private UserDao userDao;
    public  UserServiceImpl(){
        userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String userPassword) {
        Connection connection = null;
        User user = null;
        connection = BaseDao.getConnection();

        user = userDao.getLoginUser(connection, userCode, userPassword);

        BaseDao.closeResource(connection, null, null);

        return user;
    }

    @Override
    public boolean updatePwd(int id, String userPassword) throws Exception {
        Connection connection = null;
        boolean flag = false;
        connection = BaseDao.getConnection();

        int execute = userDao.updatePwd(connection, id, userPassword);
        if(execute > 0){
            flag = true;
        }
        BaseDao.closeResource(connection, null, null);
        return flag;
    }

    // 查询记录数
    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection = null;
        int count = 0;
        try {
            connection = BaseDao.getConnection();
            count = userDao.getUserCount(connection, userName, userRole);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }

        return count;
    }

    // 根据条件查询用户列表
    @Override
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        // TODO Auto-generated method stub
        Connection connection = null;
        List<User> userList = null;

        try {
            connection = BaseDao.getConnection();
            userList = userDao.getUserList(connection, queryUserName,queryUserRole,currentPageNo,pageSize);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            BaseDao.closeResource(connection, null, null);
        }
        return userList;
    }

    @Test
    public void test(){
        UserServiceImpl userService = new UserServiceImpl();
        int userCount = userService.getUserCount(null, 0);
        System.out.println("总数： " + userCount);
    }

}
