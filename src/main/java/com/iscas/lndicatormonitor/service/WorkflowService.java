package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.domain.Workflow;
import com.iscas.lndicatormonitor.dto.WorkflowDTO;

public interface WorkflowService extends IService<Workflow>  {
    int workflowInsert(WorkflowDTO workflowDTO) throws Exception;

    Workflow getWorkflowById(int workflowId) throws Exception;

    Boolean workflowUpdate(WorkflowDTO workflowDTO) throws  Exception;
}
