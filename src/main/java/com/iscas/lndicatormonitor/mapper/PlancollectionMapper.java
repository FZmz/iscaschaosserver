package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Plancollection;

public interface PlancollectionMapper {
    int deleteByPrimaryKey(Integer id);
    int deleteByPlanId(Integer planId);

    int insert(Plancollection record);

    int insertSelective(Plancollection record);

    Plancollection selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plancollection record);

    int updateByPrimaryKey(Plancollection record);


}