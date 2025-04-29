package com.iscas.lndicatormonitor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iscas.lndicatormonitor.common.CustomResult;
import com.iscas.lndicatormonitor.domain.faultconfigv2.FaultConfigV2;
import com.iscas.lndicatormonitor.dto.FaultNodeSpecDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigDTO;
import com.iscas.lndicatormonitor.dto.faultConfigv2.FaultConfigv2QueryCriteria;

import java.util.List;

/**
* @author mj
* @description 针对表【fault_config_v2】的数据库操作Service
* @createDate 2025-01-17 20:39:50
*/
public interface FaultConfigV2Service extends IService<FaultConfigV2> {
    CustomResult addFaultConfig(FaultConfigDTO faultConfigDTO);
    CustomResult getFaultConfigDetail(Integer faultConfigId);
    CustomResult updateFaultConfig(FaultConfigDTO faultConfigDTO);
    CustomResult deleteFaultConfig(Integer faultConfigId);
    CustomResult queryFaultConfigList(FaultConfigv2QueryCriteria criteria);
    Page<FaultNodeSpecDTO> queryFaultConfigV2Node(FaultConfigv2QueryCriteria criteria);

}
