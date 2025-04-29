package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.IndexRecommend.FaultMetricMapping;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author mj
* @description 针对表【fault_metric_mapping】的数据库操作Service
* @createDate 2024-12-02 16:44:17
*/
public interface FaultMetricMappingService extends IService<FaultMetricMapping> {

    List<Integer> getMetricIdsByFaultId(Integer faultId);
}
