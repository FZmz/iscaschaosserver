package com.iscas.lndicatormonitor.service.impl;

import com.iscas.lndicatormonitor.domain.ObservedIndex;
import com.iscas.lndicatormonitor.dto.IndexsDTO;
import com.iscas.lndicatormonitor.mapper.ObservedIndexMapper;
import com.iscas.lndicatormonitor.service.ObservedIndexService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObservedIndexServiceImpl implements ObservedIndexService {
    @Autowired
    ObservedIndexMapper observedIndexMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return observedIndexMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(IndexsDTO record) {
        ObservedIndex observedIndex = new ObservedIndex();
        BeanUtils.copyProperties(record,observedIndex);
        int affectRows = observedIndexMapper.insert(observedIndex);
        return affectRows;
    }

    @Override
    public int insertSelective(ObservedIndex record) {
        return 0;
    }

    @Override
    public ObservedIndex selectByPrimaryKey(Integer id) {
        return observedIndexMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<ObservedIndex> selectByFaultConfigId(Integer faultConfigId) {
        return observedIndexMapper.selectByFaultConfigId(faultConfigId);
    }

    @Override
    public int updateByPrimaryKeySelective(ObservedIndex record) {
        return observedIndexMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(ObservedIndex record) {
        return 0;
    }
}
