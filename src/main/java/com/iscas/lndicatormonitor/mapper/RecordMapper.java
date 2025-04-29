package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Record;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    int deleteByPrimaryKey(Integer id);

    int insert(Record record);

    int insertSelective(Record record);

    Record selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Record record);

    int updateByPrimaryKey(Record record);

    List<Record> selectByPlanId(Integer planId);

    List<Record> selectAll();

    int getStatus1();
    int getStatus2();
    int getStatus3();

    int getNumOfName(int player_id);

    List<Map<String, Object>> getNumOfTime();
    List<Map<String, Object>> getCount();

    int checkPlanIdExists(Integer planId );
}