package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.HistoryPwd;

import java.util.List;

public interface HistoryPwdService{

    List<HistoryPwd> selectLastFiveByUserId(Integer userId);
    int deleteByPrimaryKey(Integer id);

    int insert(HistoryPwd record);

    int insertSelective(HistoryPwd record);

    HistoryPwd selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HistoryPwd record);

    int updateByPrimaryKey(HistoryPwd record);

}
