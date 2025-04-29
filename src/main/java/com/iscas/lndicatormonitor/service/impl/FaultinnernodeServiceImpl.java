package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.domain.Faultinnernode;
import com.iscas.lndicatormonitor.domain.Linkrequest;
import com.iscas.lndicatormonitor.mapper.FaultinnernodeMapper;
import com.iscas.lndicatormonitor.mapper.LinkrequestMapper;
import com.iscas.lndicatormonitor.service.FaultinnernodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FaultinnernodeServiceImpl implements FaultinnernodeService {
    @Autowired
    FaultinnernodeMapper faultinnernodeMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int deleteByFaultConfigId(Integer id) {
        return faultinnernodeMapper.deleteByFaultConfigId(id);
    }

    @Override
    public int insert(Faultinnernode record) {
        int affectedRows =  faultinnernodeMapper.insert(record);
        return affectedRows;
    }

    @Override
    public int insertSelective(Faultinnernode record) {
        return 0;
    }

    @Override
    public Faultinnernode selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    public List<Faultinnernode> selectByFaultConfigId(Integer id) {
        return faultinnernodeMapper.selectByFaultConfigId(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Faultinnernode record) {
        return faultinnernodeMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Faultinnernode record) {
        return 0;
    }


}
