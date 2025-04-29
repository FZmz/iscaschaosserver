package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Faultinnernode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaultinnernodeMapper {
    int deleteByPrimaryKey(Integer id);
    int deleteByFaultConfigId(Integer faultConfigId);

    int insert(Faultinnernode record);

    int insertSelective(Faultinnernode record);

    Faultinnernode selectByPrimaryKey(Integer id);

    List<Faultinnernode> selectByFaultConfigId(Integer faultConfigId);

    int updateByPrimaryKeySelective(Faultinnernode record);

    int updateByPrimaryKey(Faultinnernode record);
}