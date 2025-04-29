package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.ObservedIndex;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ObservedIndexMapper {
    int deleteByPrimaryKey(Integer id);


    int insert(ObservedIndex record);

    int insertSelective(ObservedIndex record);

    ObservedIndex selectByPrimaryKey(Integer id);

    List<ObservedIndex> selectByFaultConfigId(Integer faultConfigId);

    int updateByPrimaryKeySelective(ObservedIndex record);

    int updateByPrimaryKey(ObservedIndex record);


}