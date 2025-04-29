package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.domain.Planduration;
import com.iscas.lndicatormonitor.mapper.PlandurationMapper;
import com.iscas.lndicatormonitor.service.PlandurationService;
@Service
public class PlandurationServiceImpl implements PlandurationService{

    @Resource
    private PlandurationMapper plandurationMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return plandurationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByPlanId(Integer planId) {
        return plandurationMapper.deleteByPlanId(planId);
    }

    @Override
    public int insert(Planduration record) {
        return plandurationMapper.insert(record);
    }

    @Override
    public int insertSelective(Planduration record) {
        return plandurationMapper.insertSelective(record);
    }

    @Override
    public Planduration selectByPrimaryKey(Integer id) {
        return plandurationMapper.selectByPrimaryKey(id);
    }
    @Override
    public String selectDurationByPlanId(Integer planId) {
        return plandurationMapper.selectDurationByPlanId(planId);
    }
    @Override
    public Planduration selectByPlanId(Integer planId) {
        return plandurationMapper.selectByPlanId(planId);
    }

    @Override
    public int updateByPrimaryKeySelective(Planduration record) {
        return plandurationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Planduration record) {
        return plandurationMapper.updateByPrimaryKey(record);
    }

}
