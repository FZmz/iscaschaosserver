package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Plan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    int deleteByPrimaryKey(Integer id);

    int insert(Plan record);

    int insertSelective(Plan record);

    Plan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Plan record);

    int updateByPrimaryKey(Plan record);

    List<Plan> selectAll();
    String getPlanNameById(int id);

    int getPlanSumNum();



}