package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Planduration;
public interface PlandurationService{
    int deleteByPrimaryKey(Integer id);
    int deleteByPlanId(Integer planId);

    int insert(Planduration record);

    int insertSelective(Planduration record);

    Planduration selectByPrimaryKey(Integer id);
    String selectDurationByPlanId(Integer planId);

    Planduration selectByPlanId(Integer planId);
    int updateByPrimaryKeySelective(Planduration record);

    int updateByPrimaryKey(Planduration record);

}
