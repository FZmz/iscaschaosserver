package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Users;
import com.iscas.lndicatormonitor.domain.Workflowcorrelation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowcorrelationMapper extends BaseMapper<Workflowcorrelation> {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(Workflowcorrelation record);

    Workflowcorrelation selectByPrimaryKey(Integer id);

    Workflowcorrelation selectByRecordId(Integer recordId);

    int updateByPrimaryKeySelective(Workflowcorrelation record);

    int updateByPrimaryKey(Workflowcorrelation record);
}