package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.Selector;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SelectorMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Selector record);

    int insertSelective(Selector record);

    Selector selectByPrimaryKey(Integer id);
    Selector selectByFaultConfigKey(Integer faultConfigId);
    int updateByPrimaryKeySelective(Selector record);

    int updateByPrimaryKey(Selector record);
}