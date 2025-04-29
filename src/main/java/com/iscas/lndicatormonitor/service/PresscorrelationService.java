package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Presscorrelation;

import java.util.List;

public interface PresscorrelationService{


    int deleteByPrimaryKey(Integer id);
    int deleteByRecordId(Integer recordId);

    int insert(Presscorrelation record);

    int insertSelective(Presscorrelation record);

    Presscorrelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Presscorrelation record);

    int updateByPrimaryKey(Presscorrelation record);

    List<Presscorrelation> selectByRecordId(Integer recordId);
}
