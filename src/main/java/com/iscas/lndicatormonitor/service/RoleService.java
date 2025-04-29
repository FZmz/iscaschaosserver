package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Role;
public interface RoleService{
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    Role selectByUserId(Integer userId);
    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

}
