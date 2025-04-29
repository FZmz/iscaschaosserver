package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.domain.Record;
import com.iscas.lndicatormonitor.mapper.RecordMapper;
import com.iscas.lndicatormonitor.service.RecordService;

import java.util.List;
import java.util.Map;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record>
        implements RecordService {

    @Resource
    private RecordMapper recordMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return recordMapper.deleteByPrimaryKey(id);
    }
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public int insert(Record record) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        int affectedRows = recordMapper.insert(record);
        if (affectedRows > 0){
            String planAsString = objectMapper.writeValueAsString(record.getId());
            rabbitTemplate.convertAndSend("", "record", planAsString);
        }
        return affectedRows;
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
    public List<Record> getRecordsByPlanId(Integer planId) {
        return recordMapper.selectByPlanId(planId);
    }

    @Override
    public List<Record> getAll() {
        return recordMapper.selectAll();
    }


    @Override
    public int updateByPrimaryKeySelective(Record record) {
        return recordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Record record) {
        return recordMapper.updateByPrimaryKey(record);
    }

    @Override
    public int getStatus1(){
        return recordMapper.getStatus1();
    };

    @Override
    public int getStatus2(){
        return recordMapper.getStatus2();
    };

    @Override
    public int getStatus3(){
        return recordMapper.getStatus3();
    };

    @Override
    public int getNumOfName(int player_id){
        return recordMapper.getNumOfName(player_id);
    };

    @Override
    public List<Map<String, Object>> getNumOfTime(){
        return recordMapper.getNumOfTime();
    };

    @Override
    public List<Map<String, Object>> getCount(){
        return recordMapper.getCount();
    };

    @Override
    public int checkPlanIdExists(int planId) {
        return recordMapper.checkPlanIdExists(planId);
    }

}
