package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Loginattempt;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginattemptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Loginattempt record);

    int insertSelective(Loginattempt record);

    Loginattempt selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Loginattempt record);

    int updateByPrimaryKey(Loginattempt record);

    Loginattempt selectByUserId(Integer userId);
}