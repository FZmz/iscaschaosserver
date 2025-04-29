package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.ObservedIndex;
import com.iscas.lndicatormonitor.dto.IndexsDTO;

import java.util.List;

public interface ObservedIndexService{


    int deleteByPrimaryKey(Integer id);

    int insert(IndexsDTO indexsDTO);

    int insertSelective(ObservedIndex record);

    ObservedIndex selectByPrimaryKey(Integer id);

    List<ObservedIndex> selectByFaultConfigId(Integer faultConfigId);
    int updateByPrimaryKeySelective(ObservedIndex record);

    int updateByPrimaryKey(ObservedIndex record);

}
