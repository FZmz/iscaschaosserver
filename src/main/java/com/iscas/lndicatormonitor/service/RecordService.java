package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.iscas.lndicatormonitor.domain.Plan;
import com.iscas.lndicatormonitor.domain.Record;

import java.util.List;
import java.util.Map;

public interface RecordService  extends IService<Record> {


    int deleteByPrimaryKey(Integer id);

    int insert(Record record) throws JsonProcessingException;

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer id);

    List<Record> getRecordsByPlanId(Integer planId);

    List<Record> getAll();
    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);

    int getStatus1();
    int getStatus2();
    int getStatus3();
    int getNumOfName(int player_id);

    List<Map<String, Object>> getNumOfTime();

    List<Map<String, Object>> getCount();

    int checkPlanIdExists(int planId);


}
