package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.domain.Observedcorrelation;
import com.iscas.lndicatormonitor.mapper.ObservedcorrelationMapper;
import com.iscas.lndicatormonitor.service.ObservedcorrelationService;

import java.util.List;

@Service
public class ObservedcorrelationServiceImpl implements ObservedcorrelationService{

    @Resource
    private ObservedcorrelationMapper observedcorrelationMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return observedcorrelationMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Observedcorrelation record) {
        return observedcorrelationMapper.insert(record);
    }

    @Override
    public int insertSelective(Observedcorrelation record) {
        return observedcorrelationMapper.insertSelective(record);
    }

    @Override
    public Observedcorrelation selectByPrimaryKey(Integer id) {
        return observedcorrelationMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Observedcorrelation> selectByRecordId(Integer recordId) {
        return observedcorrelationMapper.selectByRecordId(recordId);
    }

    @Override
    public int updateByPrimaryKeySelective(Observedcorrelation record) {
        return observedcorrelationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Observedcorrelation record) {
        return observedcorrelationMapper.updateByPrimaryKey(record);
    }

}
