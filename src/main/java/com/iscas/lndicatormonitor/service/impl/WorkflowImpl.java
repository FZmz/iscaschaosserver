package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Linkrequest;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.Workflow;
import com.iscas.lndicatormonitor.dto.WorkflowDTO;
import com.iscas.lndicatormonitor.mapper.LinkrequestMapper;
import com.iscas.lndicatormonitor.mapper.Planv2Mapper;
import com.iscas.lndicatormonitor.mapper.WorkflowMapper;
import com.iscas.lndicatormonitor.service.LinkrequestService;
import com.iscas.lndicatormonitor.service.WorkflowService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WorkflowImpl extends ServiceImpl<WorkflowMapper, Workflow>
         implements WorkflowService {
    @Autowired
    WorkflowMapper workflowMapper;
    @Override
    public int workflowInsert(WorkflowDTO workflowDTO) throws Exception {
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(workflowDTO,workflow);
        workflowMapper.insert(workflow);
        return workflow.getId();
    }

    @Override
    public Workflow getWorkflowById(int workflowId) throws Exception {
        Workflow workflow;
        workflow = workflowMapper.selectByPrimaryKey(workflowId);
        return workflow;
    }

    @Override
    public Boolean workflowUpdate(WorkflowDTO workflowDTO) throws Exception {
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(workflowDTO, workflow);
        int affectedRows = workflowMapper.updateByPrimaryKey(workflow);
        if (affectedRows > 0) {
            return true; // 更新成功
        } else {
            return false; // 更新失败
        }
    }
}
