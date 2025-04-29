package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.Connection;
import com.iscas.lndicatormonitor.mapper.ConnectionMapper;
import com.iscas.lndicatormonitor.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ConnectionServiceImpl extends ServiceImpl<ConnectionMapper, Connection> implements ConnectionService {

    @Autowired
    private ConnectionMapper connectionMapper;
    @Override
    public List<Connection> getByApplicationId(String applicationId) {
        if (applicationId != null) {
            QueryWrapper<Connection> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("application_id", applicationId);
            return baseMapper.selectList(queryWrapper);
        }
        return Collections.emptyList();
    }
}
