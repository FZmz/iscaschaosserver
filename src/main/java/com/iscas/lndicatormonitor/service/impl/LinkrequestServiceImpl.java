package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.domain.Linkrequest;
import com.iscas.lndicatormonitor.mapper.LinkrequestMapper;
import com.iscas.lndicatormonitor.service.LinkrequestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public  class LinkrequestServiceImpl implements LinkrequestService {

    @Resource
    private LinkrequestMapper linkrequestMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return linkrequestMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Linkrequest record) {
        return linkrequestMapper.insert(record);
    }

    @Override
    public int insertSelective(Linkrequest record) {
        return linkrequestMapper.insertSelective(record);
    }

    @Override
    public Linkrequest selectByPrimaryKey(Integer id) {
        return linkrequestMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(Linkrequest record) {
        return linkrequestMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Linkrequest record) {
        return linkrequestMapper.updateByPrimaryKey(record);
    }

}
