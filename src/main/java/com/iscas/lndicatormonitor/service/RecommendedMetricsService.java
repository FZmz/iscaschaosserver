package com.iscas.lndicatormonitor.service;

import com.iscas.lndicatormonitor.domain.IndexRecommend.RecommendedMetrics;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author mj
* @description 针对表【recommended_metrics】的数据库操作Service
* @createDate 2024-12-02 16:44:17
*/
public interface RecommendedMetricsService extends IService<RecommendedMetrics> {
    List<RecommendedMetrics> getMetricsByIds(List<Integer> metricIds);
    /**
     * 根据指标名称获取指标详情
     *
     * @param metricName 指标名称
     * @return RecommendedMetrics 对象列表
     */
    List<RecommendedMetrics> getMetricsByMetricName(String metricName);
}
