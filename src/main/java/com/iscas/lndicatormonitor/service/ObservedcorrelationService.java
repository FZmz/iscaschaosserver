package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.Observedcorrelation;

import java.util.List;

public interface ObservedcorrelationService{


    int deleteByPrimaryKey(Integer id);

    int insert(Observedcorrelation record);

    int insertSelective(Observedcorrelation record);

    Observedcorrelation selectByPrimaryKey(Integer id);

    List<Observedcorrelation> selectByRecordId(Integer recordId);

    int updateByPrimaryKeySelective(Observedcorrelation record);

    int updateByPrimaryKey(Observedcorrelation record);

}
