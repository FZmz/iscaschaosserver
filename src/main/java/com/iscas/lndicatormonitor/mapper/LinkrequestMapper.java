package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Linkrequest;

public interface LinkrequestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Linkrequest record);

    int insertSelective(Linkrequest record);

    Linkrequest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Linkrequest record);

    int updateByPrimaryKey(Linkrequest record);
}