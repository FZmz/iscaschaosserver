package com.iscas.lndicatormonitor.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.mapper.PresscorrelationMapper;
import com.iscas.lndicatormonitor.domain.Presscorrelation;
import com.iscas.lndicatormonitor.service.PresscorrelationService;

import java.util.List;

@Service
public class PresscorrelationServiceImpl implements PresscorrelationService{

    @Resource
    private PresscorrelationMapper presscorrelationMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return presscorrelationMapper.deleteByPrimaryKey(id);
    }
    public int deleteByRecordId(Integer recordId ){
        return presscorrelationMapper.deleteByRecordId(recordId);
    }

    @Override
    public int insert(Presscorrelation record) {
        return presscorrelationMapper.insert(record);
    }

    @Override
    public int insertSelective(Presscorrelation record) {
        return presscorrelationMapper.insertSelective(record);
    }

    @Override
    public Presscorrelation selectByPrimaryKey(Integer id) {
        return presscorrelationMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Presscorrelation record) {
        return presscorrelationMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Presscorrelation record) {
        return presscorrelationMapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Presscorrelation> selectByRecordId(Integer recordId) {
        return presscorrelationMapper.selectByRecordId(recordId);
    }

}
