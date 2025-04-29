package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Plancollection;
public interface PlancollectionService{


    int deleteByPrimaryKey(Integer id);

    int insert(Plancollection record);

    int insertSelective(Plancollection record);

    Plancollection selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plancollection record);

    int updateByPrimaryKey(Plancollection record);

    int deleteByPlanId(Integer planId);

}
