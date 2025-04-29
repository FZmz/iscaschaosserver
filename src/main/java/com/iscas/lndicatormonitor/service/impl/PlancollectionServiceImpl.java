package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.service.PlancollectionService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.PlancollectionMapper;
import com.iscas.lndicatormonitor.domain.Plancollection;

@Service
public class PlancollectionServiceImpl implements PlancollectionService {

    @Resource
    private PlancollectionMapper plancollectionMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return plancollectionMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Plancollection record) {
        return plancollectionMapper.insert(record);
    }

    @Override
    public int insertSelective(Plancollection record) {
        return plancollectionMapper.insertSelective(record);
    }

    @Override
    public Plancollection selectByPrimaryKey(Integer id) {
        return plancollectionMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Plancollection record) {
        return plancollectionMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Plancollection record) {
        return plancollectionMapper.updateByPrimaryKey(record);
    }

    @Override
    public int deleteByPlanId(Integer planId) {
        return plancollectionMapper.deleteByPlanId(planId);
    }


}
