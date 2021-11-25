package com.codeleopard.dao.role;

import com.codeleopard.dao.BaseDao;
import com.codeleopard.pojo.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    // 获取角色列表
    @Override
    public List<Role> getRoleList(Connection connection) throws Exception {

        PreparedStatement pstm = null;
        ResultSet rs = null;
        ArrayList<Role> roleList = new ArrayList<>();

        if(connection != null){
            String sql = "select * from smbms_role";
            // 和用户列表进行拼接
            Object[] params = {};

            rs = BaseDao.execute(connection, pstm, rs, sql, params);

            while (rs.next()){
                Role _role = new Role();

                _role.setId(rs.getInt("id"));
                _role.setRoleName(rs.getString("roleName"));
                _role.setRoleCode(rs.getString("roleCode"));
                roleList.add(_role);
            }
            // 关闭资源
            BaseDao.closeResource(null, pstm, rs);
        }
        return roleList;
    }
}
