package com.iscas.lndicatormonitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iscas.lndicatormonitor.domain.faultconfigv2.ConfigTarget;

/**
* @author mj
* @description 针对表【config_target】的数据库操作Mapper
* @createDate 2025-01-17 20:39:50
* @Entity generator.domain.ConfigTarget
*/
public interface ConfigTargetMapper extends BaseMapper<ConfigTarget> {
    ConfigTarget selectByFaultConfigId(Integer faultConfigId);

}




