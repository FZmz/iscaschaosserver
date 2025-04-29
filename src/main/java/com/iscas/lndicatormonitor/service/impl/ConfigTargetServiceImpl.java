package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.iscas.lndicatormonitor.domain.faultconfigv2.ConfigTarget;
import com.iscas.lndicatormonitor.mapper.ConfigTargetMapper;
import com.iscas.lndicatormonitor.service.ConfigTargetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author mj
* @description 针对表【config_target】的数据库操作Service实现
* @createDate 2025-01-17 20:39:50
*/
@Service
public class ConfigTargetServiceImpl extends ServiceImpl<ConfigTargetMapper, ConfigTarget>
    implements ConfigTargetService {

    @Resource
    private ConfigTargetMapper configTargetMapper;
    @Override
    public ConfigTarget selectByFaultConfigId(Integer faultConfigId)   {

        return configTargetMapper.selectByFaultConfigId(faultConfigId);
    }

}




