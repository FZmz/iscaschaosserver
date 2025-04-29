package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Connection;
import com.iscas.lndicatormonitor.domain.Service;
import com.iscas.lndicatormonitor.domain.Statebound;
import com.iscas.lndicatormonitor.mapper.ServiceMapper;
import com.iscas.lndicatormonitor.mapper.StateboundMapper;
import com.iscas.lndicatormonitor.service.ServiceService;
import com.iscas.lndicatormonitor.service.StateboundService;

import java.util.Collections;
import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServiceImpl extends ServiceImpl<ServiceMapper, Service> implements ServiceService {
    @Override
    public List<Service> getByApplicationId(String applicationId) {
        if (applicationId != null) {
            QueryWrapper<Service> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("application_id", applicationId);
            return baseMapper.selectList(queryWrapper);
        }
        return Collections.emptyList();
    }
}
