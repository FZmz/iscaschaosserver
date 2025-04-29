package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Faultconfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaultconfigMapper extends BaseMapper<Faultconfig> {
    int deleteByPrimaryKey(Integer id);

    int insert(Faultconfig record);

    int insertSelective(Faultconfig record);

    Faultconfig selectByPrimaryKey(Integer id);

    String selectFaultTagByName(String name);
    int updateByPrimaryKeySelective(Faultconfig record);

    int updateByPrimaryKey(Faultconfig record);

    List<Faultconfig> selectAll();

    List<Faultconfig> selectFaultconfigByName(String faultConfigName);
}