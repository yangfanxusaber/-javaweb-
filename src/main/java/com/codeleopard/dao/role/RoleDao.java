package com.codeleopard.dao.role;

import com.codeleopard.pojo.Role;

import java.sql.Connection;
import java.util.List;

public interface RoleDao {

    // 获取角色列表 userRole
    public List<Role> getRoleList(Connection connection) throws Exception;
}
