package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.Faultcorrelation;
import com.iscas.lndicatormonitor.domain.Statebound;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FaultcorrelationMapper extends BaseMapper<Faultcorrelation> {
    int deleteByPrimaryKey(Integer id);
    int deleteByPlanId(Integer planId);



    int insert(Faultcorrelation record);

    int insertSelective(Faultcorrelation record);

    Faultcorrelation selectByPrimaryKey(Integer id);

    List<Faultcorrelation> selectByPlanId(Integer planId);

    int updateByPrimaryKeySelective(Faultcorrelation record);

    int updateByPrimaryKey(Faultcorrelation record);
    int checkFaultConfigIdExists(int faultConfigId );


}