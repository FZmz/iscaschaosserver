package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Presscorrelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PresscorrelationMapper {
    int deleteByPrimaryKey(Integer id);
    int deleteByRecordId(Integer recordId);

    int insert(Presscorrelation record);

    int insertSelective(Presscorrelation record);

    Presscorrelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Presscorrelation record);

    int updateByPrimaryKey(Presscorrelation record);
    List<Presscorrelation> selectByRecordId(Integer recordId);
}