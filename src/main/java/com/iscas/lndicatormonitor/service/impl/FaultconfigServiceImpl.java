package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.service.ConnectionService;
import com.iscas.lndicatormonitor.service.FaultconfigService;
import com.iscas.lndicatormonitor.service.PrometheusIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.iscas.lndicatormonitor.domain.Faultconfig;
import com.iscas.lndicatormonitor.mapper.FaultconfigMapper;

import java.util.List;

@Service
public class FaultconfigServiceImpl extends ServiceImpl<FaultconfigMapper, Faultconfig> implements FaultconfigService  {

    @Autowired
    FaultconfigMapper faultconfigMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(Faultconfig record) {
        int affectRows =  faultconfigMapper.insert(record);
        // 新增
        return affectRows;
    }

    @Override
    public int insertSelective(Faultconfig record) {
        return 0;
    }

    @Override
    public Faultconfig selectByPrimaryKey(Integer id) {
        return faultconfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public String selectFaultTagByName(String faultConfigName) {
        return faultconfigMapper.selectFaultTagByName(faultConfigName);
    }

    @Override
    public List<Faultconfig> selectAll() {
        return faultconfigMapper.selectAll();
    }

    @Override
    public int updateByPrimaryKeySelective(Faultconfig record) {
        return faultconfigMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Faultconfig record) {
        return 0;
    }

    @Override
    public List<Faultconfig> selectFaultconfigByName(String name) {
        List<Faultconfig> faultconfigList = faultconfigMapper.selectFaultconfigByName(name);
         return faultconfigList;
    }


}
