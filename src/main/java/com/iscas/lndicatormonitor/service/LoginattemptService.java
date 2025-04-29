package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Loginattempt;
public interface LoginattemptService{
    int deleteByPrimaryKey(Integer id);
    int insert(Loginattempt record);
    int insertSelective(Loginattempt record);
    Loginattempt selectByPrimaryKey(Integer id);

    Loginattempt selectByUserId(Integer userId);
    int updateByPrimaryKeySelective(Loginattempt record);
    int updateByPrimaryKey(Loginattempt record);

}
