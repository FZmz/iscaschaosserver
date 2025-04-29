package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Faultinnernode;

import java.util.List;

public interface FaultinnernodeService{


    int deleteByPrimaryKey(Integer id);

    int deleteByFaultConfigId(Integer id);
    int insert(Faultinnernode record);

    int insertSelective(Faultinnernode record);

    Faultinnernode selectByPrimaryKey(Integer id);

    List<Faultinnernode> selectByFaultConfigId(Integer id);
    int updateByPrimaryKeySelective(Faultinnernode record);

    int updateByPrimaryKey(Faultinnernode record);

}
