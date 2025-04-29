package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    int deleteByPrimaryKey(Integer id);
    int deleteByUserId(Integer userId);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    Role selectByUserId(Integer userId);

    List<Role> selectAllRole();

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);


}