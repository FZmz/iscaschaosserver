package com.iscas.lndicatormonitor.handler.IndexRecommend.coroot;

import com.iscas.lndicatormonitor.domain.IndexRecommend.RecommendedMetrics;
import com.iscas.lndicatormonitor.dto.indexRecommend.PrometheusProcessorDTO;
import com.iscas.lndicatormonitor.handler.MetricSourceHandler;
import com.iscas.lndicatormonitor.processor.IndexRecommend.coroot.CorootApplicationProcessor;
import com.iscas.lndicatormonitor.processor.IndexRecommend.coroot.CorootNodeProcessor;
import com.iscas.lndicatormonitor.service.RecommendedMetricsService;
import com.iscas.lndicatormonitor.utils.K8sUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CorootHandler implements MetricSourceHandler {

    private static final Logger log = LoggerFactory.getLogger(CorootHandler.class);
    @Autowired
    private CorootNodeProcessor nodeProcessor;

    @Autowired
    private CorootApplicationProcessor applicationProcessor;

    @Autowired
    private K8sUtil k8sUtil;

    @Autowired
    private RecommendedMetricsService recommendedMetricsService;

    @Override
    public Object handle(PrometheusProcessorDTO dto) throws Exception {
        // 根据 namespace 和 podName 获取 serviceName
        RecommendedMetrics recommendedMetrics = recommendedMetricsService.getMetricsByMetricName(dto.getIndexName()).get(0);
        // 判断是否是节点指标
        if (dto.getIndexName().toLowerCase().contains("node")) {
            log.info("node: {} " , recommendedMetrics);
            return nodeProcessor.getWidgetData(dto.getInstance(), dto.getFrom(), dto.getTo(), recommendedMetrics.getOperationPath());
        } else {
            String serviceName = k8sUtil.getServiceNameByNamespaceAndPodName(dto.getNamespace(), dto.getPodName());
            if (serviceName == null) {
                throw new IllegalArgumentException("Service name not found for namespace: " + dto.getNamespace() + ", podName: " + dto.getPodName());
            }
            log.info("non-node: {} " , recommendedMetrics);
            return applicationProcessor.getChartData(serviceName, dto.getNamespace(), "Deployment", dto.getFrom(), dto.getTo(), recommendedMetrics.getOperationPath());
        }
    }
}