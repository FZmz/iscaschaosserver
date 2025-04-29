package com.iscas.lndicatormonitor.mapper;

import com.iscas.lndicatormonitor.domain.IndexRecommend.RecommendedMetrics;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author mj
* @description 针对表【recommended_metrics】的数据库操作Mapper
* @createDate 2024-12-02 16:44:17
* @Entity com.iscas.lndicatormonitor.domain.IndexRecommand.RecommendedMetrics
*/
@Mapper
public interface RecommendedMetricsMapper extends BaseMapper<RecommendedMetrics> {

    /**
     * 根据指标名称获取指标详情
     *
     * @param metricName 指标名称
     * @return RecommendedMetrics 对象列表
     */
    @Select("SELECT * FROM recommended_metrics WHERE metric_name = #{metricName}")
    List<RecommendedMetrics> getMetricsByMetricName(@Param("metricName") String metricName);
}