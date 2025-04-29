package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Observedcorrelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ObservedcorrelationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Observedcorrelation record);

    int insertSelective(Observedcorrelation record);

    Observedcorrelation selectByPrimaryKey(Integer id);

    List<Observedcorrelation> selectByRecordId(Integer recordId);

    int updateByPrimaryKeySelective(Observedcorrelation record);

    int updateByPrimaryKey(Observedcorrelation record);
}