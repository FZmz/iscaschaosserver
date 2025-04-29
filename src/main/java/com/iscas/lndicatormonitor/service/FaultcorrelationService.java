package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.domain.Faultcorrelation;
import com.iscas.lndicatormonitor.domain.Planv2;

import java.util.List;

public interface FaultcorrelationService extends IService<Faultcorrelation> {


    int deleteByPrimaryKey(Integer id);
    int deleteByPlanId(Integer id);

    int insert(Faultcorrelation record);

    int insertSelective(Faultcorrelation record);

    Faultcorrelation selectByPrimaryKey(Integer id);

    List<Faultcorrelation> selectByPlanId(Integer planId);
    int updateByPrimaryKeySelective(Faultcorrelation record);

    int updateByPrimaryKey(Faultcorrelation record);

    int checkFaultConfigIdExists(int faultConfigId);


}
