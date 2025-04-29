package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.IndexRecommend.RecommendedMetrics;
import com.iscas.lndicatormonitor.service.RecommendedMetricsService;
import com.iscas.lndicatormonitor.mapper.RecommendedMetricsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author mj
* @description 针对表【recommended_metrics】的数据库操作Service实现
* @createDate 2024-12-02 16:44:17
*/
@Service
public class RecommendedMetricsServiceImpl extends ServiceImpl<RecommendedMetricsMapper, RecommendedMetrics>
    implements RecommendedMetricsService{
    private final RecommendedMetricsMapper recommendedMetricsMapper;

    public RecommendedMetricsServiceImpl(RecommendedMetricsMapper recommendedMetricsMapper) {
        this.recommendedMetricsMapper = recommendedMetricsMapper;
    }

    @Override
    public List<RecommendedMetrics> getMetricsByIds(List<Integer> metricIds) {
        // 使用 MyBatis-Plus 提供的批量查询方法
        return recommendedMetricsMapper.selectBatchIds(metricIds);
    }

    @Override
    public List<RecommendedMetrics> getMetricsByMetricName(String metricName) {
        return recommendedMetricsMapper.getMetricsByMetricName(metricName);
    }
}


