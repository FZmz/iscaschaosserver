package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.Users;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:00
 */
@Mapper
public interface UsersMapper extends BaseMapper<Users> {
    int deleteByPrimaryKey(Integer id);

    int insert(Users record);

    int insertSelective(Users record);

    Users selectByPrimaryKey(Integer id);

    Users selectByUserName(String username);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    List<Users> getAll();

    int checkUsernameExists(String username);

    int updatePwdById(@Param("id") Integer id, @Param("password") String password);
}