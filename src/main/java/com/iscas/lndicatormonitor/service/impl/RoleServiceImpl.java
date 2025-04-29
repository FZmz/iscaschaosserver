package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.RoleMapper;
import com.iscas.lndicatormonitor.domain.Role;
import com.iscas.lndicatormonitor.service.RoleService;
@Service
public class RoleServiceImpl implements RoleService{

    @Resource
    private RoleMapper roleMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Role record) {
        return roleMapper.insert(record);
    }

    @Override
    public int insertSelective(Role record) {
        return roleMapper.insertSelective(record);
    }

    @Override
    public Role selectByPrimaryKey(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    @Override
    public Role selectByUserId(Integer userId) {
        return roleMapper.selectByUserId(userId);
    }

    @Override
    public int updateByPrimaryKeySelective(Role record) {
        return roleMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Role record) {
        return roleMapper.updateByPrimaryKey(record);
    }

}
