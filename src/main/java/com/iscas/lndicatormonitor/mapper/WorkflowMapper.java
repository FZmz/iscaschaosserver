package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.domain.Workflow;

public interface WorkflowMapper extends BaseMapper<Workflow> {
    int deleteByPrimaryKey(Integer id);

    int insert(Workflow record);

    int insertSelective(Workflow record);

    Workflow selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Workflow record);

    int updateByPrimaryKey(Workflow record);
}