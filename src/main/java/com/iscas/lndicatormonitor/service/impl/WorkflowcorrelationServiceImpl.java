package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.mapper.UsersMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.WorkflowcorrelationMapper;
import com.iscas.lndicatormonitor.domain.Workflowcorrelation;
import com.iscas.lndicatormonitor.service.WorkflowcorrelationService;
@Service
public class WorkflowcorrelationServiceImpl extends ServiceImpl<WorkflowcorrelationMapper, Workflowcorrelation> implements WorkflowcorrelationService{

    @Resource
    private WorkflowcorrelationMapper workflowcorrelationMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return workflowcorrelationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertSelective(Workflowcorrelation record) {
        return workflowcorrelationMapper.insertSelective(record);
    }

    @Override
    public Workflowcorrelation selectByPrimaryKey(Integer id) {
        return workflowcorrelationMapper.selectByPrimaryKey(id);
    }

    @Override
    public Workflowcorrelation selectByRecordId(Integer recordId) {
        return workflowcorrelationMapper.selectByRecordId(recordId);
    }

    @Override
    public int updateByPrimaryKeySelective(Workflowcorrelation record) {
        return workflowcorrelationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Workflowcorrelation record) {
        return workflowcorrelationMapper.updateByPrimaryKey(record);
    }

}
