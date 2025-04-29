package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.*;
import com.iscas.lndicatormonitor.mapper.PlanMapper;
import com.iscas.lndicatormonitor.mapper.RecordMapper;
import com.iscas.lndicatormonitor.mapper.WorkflowMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Yukun Hou
 * @create 2023-10-10 15:38
 */
public interface PrometheusIndexService {


    Object getPrometheusIndex(PrometheusIndex prometheusIndex) throws Exception;

    Object getNodeIndex(PrometheusIndex prometheusIndex) throws Exception;

    interface RecordService{


        int deleteByPrimaryKey(Integer id);

        int insert(Record record);

        int insertSelective(Record record);

        Record selectByPrimaryKey(Integer id);

        int updateByPrimaryKeySelective(Record record);

        int updateByPrimaryKey(Record record);


        @Service
        class RecordServiceImpl implements RecordService {

            @Resource
            private RecordMapper recordMapper;

            @Override
            public int deleteByPrimaryKey(Integer id) {
                return recordMapper.deleteByPrimaryKey(id);
            }

            @Override
            public int insert(Record record) {
                return recordMapper.insert(record);
            }

            @Override
            public int insertSelective(Record record) {
                return recordMapper.insertSelective(record);
            }

            @Override
            public Record selectByPrimaryKey(Integer id) {
                return recordMapper.selectByPrimaryKey(id);
            }

            @Override
            public int updateByPrimaryKeySelective(Record record) {
                return recordMapper.updateByPrimaryKeySelective(record);
            }

            @Override
            public int updateByPrimaryKey(Record record) {
                return recordMapper.updateByPrimaryKey(record);
            }

        }

        @Service
        class WorkflowServiceImpl implements WorkflowService {

            @Resource
            private WorkflowMapper workflowMapper;

            @Override
            public int deleteByPrimaryKey(Integer id) {
                return workflowMapper.deleteByPrimaryKey(id);
            }

            @Override
            public int insert(Workflow record) {
                return workflowMapper.insert(record);
            }

            @Override
            public int insertSelective(Workflow record) {
                return workflowMapper.insertSelective(record);
            }

            @Override
            public Workflow selectByPrimaryKey(Integer id) {
                return workflowMapper.selectByPrimaryKey(id);
            }

            @Override
            public int updateByPrimaryKeySelective(Workflow record) {
                return workflowMapper.updateByPrimaryKeySelective(record);
            }

            @Override
            public int updateByPrimaryKey(Workflow record) {
                return workflowMapper.updateByPrimaryKey(record);
            }

        }
    }

    interface WorkflowService{


        int deleteByPrimaryKey(Integer id);

        int insert(Workflow record);

        int insertSelective(Workflow record);

        Workflow selectByPrimaryKey(Integer id);

        int updateByPrimaryKeySelective(Workflow record);

        int updateByPrimaryKey(Workflow record);

    }

    interface FaultconfigService{


        int deleteByPrimaryKey(Integer id);

        int insert(Faultconfig record);

        int insertSelective(Faultconfig record);

        Faultconfig selectByPrimaryKey(Integer id);

        int updateByPrimaryKeySelective(Faultconfig record);

        int updateByPrimaryKey(Faultconfig record);

    }
}
