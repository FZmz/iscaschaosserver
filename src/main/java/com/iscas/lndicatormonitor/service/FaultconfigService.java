package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.Faultconfig;
import com.iscas.lndicatormonitor.domain.Plan;

import java.util.List;

public interface FaultconfigService  extends IService<Faultconfig> {


    int deleteByPrimaryKey(Integer id);

    int insert(Faultconfig record);

    int insertSelective(Faultconfig record);

    Faultconfig selectByPrimaryKey(Integer id);

    String selectFaultTagByName(String faultConfigName);

    List<Faultconfig> selectAll();

    int updateByPrimaryKeySelective(Faultconfig record);

    int updateByPrimaryKey(Faultconfig record);

    List<Faultconfig> selectFaultconfigByName(String name);


}
