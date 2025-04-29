package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.domain.Selector;
import com.iscas.lndicatormonitor.mapper.SelectorMapper;
import com.iscas.lndicatormonitor.service.SelectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SelectorServiceImpl implements SelectorService {
    @Autowired
    SelectorMapper selectorMapper;
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return 0;
    }

    @Override
    public int insert(Selector selector) {
//        selector.setNamespace(selectorDTO.getNamespace());
//        selector.setLabels(String.join(",", selector.getLabels()));
        int affectedRows =  selectorMapper.insert(selector);
        return affectedRows;
    }

    @Override
    public int insertSelective(Selector record) {
        return 0;
    }

    @Override
    public Selector selectByPrimaryKey(Integer id) {
        return null;
    }

    @Override
    public Selector selectByFaultConfigKey(Integer faultConfigId) {
        return selectorMapper.selectByFaultConfigKey(faultConfigId);
    }

    @Override
    public int updateByPrimaryKeySelective(Selector record) {
        return selectorMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Selector record) {
        return 0;
    }
}
