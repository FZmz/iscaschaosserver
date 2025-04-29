package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.mapper.FaultconfigMapper;
import com.iscas.lndicatormonitor.mapper.Planv2Mapper;
import com.iscas.lndicatormonitor.service.Planv2Service;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.domain.Faultcorrelation;
import com.iscas.lndicatormonitor.mapper.FaultcorrelationMapper;
import com.iscas.lndicatormonitor.service.FaultcorrelationService;

import java.util.List;

@Service
public class FaultcorrelationServiceImpl extends ServiceImpl<FaultcorrelationMapper, Faultcorrelation>
         implements FaultcorrelationService{

    @Resource
    private FaultcorrelationMapper faultcorrelationMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return faultcorrelationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByPlanId(Integer planId) {
        return faultcorrelationMapper.deleteByPlanId(planId);
    }

    @Override
    public int insert(Faultcorrelation record) {
        return faultcorrelationMapper.insert(record);
    }

    @Override
    public int insertSelective(Faultcorrelation record) {
        return faultcorrelationMapper.insertSelective(record);
    }

    @Override
    public Faultcorrelation selectByPrimaryKey(Integer id) {
        return faultcorrelationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Faultcorrelation> selectByPlanId(Integer planId) {
        return faultcorrelationMapper.selectByPlanId(planId);
    }

    @Override
    public int updateByPrimaryKeySelective(Faultcorrelation record) {
        return faultcorrelationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Faultcorrelation record) {
        return faultcorrelationMapper.updateByPrimaryKey(record);
    }

    @Override
    public int checkFaultConfigIdExists(int faultConfigId) {
        return faultcorrelationMapper.checkFaultConfigIdExists(faultConfigId);
    }


}
