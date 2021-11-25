package com.codeleopard.service.role;

import com.codeleopard.dao.BaseDao;
import com.codeleopard.dao.role.RoleDao;
import com.codeleopard.dao.role.RoleDaoImpl;
import com.codeleopard.pojo.Role;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{

    // 引入Dao
    private RoleDao roleDao;
    public RoleServiceImpl() {
        this.roleDao =  new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;

        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection, null, null);
        }
        return  roleList;
    }

    @Test
    public void test(){
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }

}
