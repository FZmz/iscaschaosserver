package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.domain.Workflowcorrelation;

public interface WorkflowcorrelationService extends IService<Workflowcorrelation> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Workflowcorrelation record);

    Workflowcorrelation selectByPrimaryKey(Integer id);

    Workflowcorrelation selectByRecordId(Integer recordId);

    int updateByPrimaryKeySelective(Workflowcorrelation record);

    int updateByPrimaryKey(Workflowcorrelation record);

}
