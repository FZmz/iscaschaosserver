package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.IndexRecommend.FaultMetricMapping;
import com.iscas.lndicatormonitor.service.FaultMetricMappingService;
import com.iscas.lndicatormonitor.mapper.FaultMetricMappingMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【fault_metric_mapping】的数据库操作Service实现
* @createDate 2024-12-02 16:44:17
*/
@Service
public class FaultMetricMappingServiceImpl extends ServiceImpl<FaultMetricMappingMapper, FaultMetricMapping>
    implements FaultMetricMappingService{

    private final FaultMetricMappingMapper faultMetricMappingMapper;

    public FaultMetricMappingServiceImpl(FaultMetricMappingMapper faultMetricMappingMapper) {
        this.faultMetricMappingMapper = faultMetricMappingMapper;
    }

    @Override
    public List<Integer> getMetricIdsByFaultId(Integer faultId) {
        return faultMetricMappingMapper.selectList(new QueryWrapper<FaultMetricMapping>().eq("fault_id", faultId))
                .stream()
                .map(FaultMetricMapping::getMetricId)
                .collect(Collectors.toList());
    }
}




