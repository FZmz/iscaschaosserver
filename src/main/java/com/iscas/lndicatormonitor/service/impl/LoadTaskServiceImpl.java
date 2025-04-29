package com.iscas.lndicatormonitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iscas.lndicatormonitor.domain.LoadCorrelation;
import com.iscas.lndicatormonitor.domain.LoadTask;
import com.iscas.lndicatormonitor.domain.recordv2.ChaosExerciseRecords;
import com.iscas.lndicatormonitor.domain.Planv2;
import com.iscas.lndicatormonitor.mapper.LoadTaskMapper;
import com.iscas.lndicatormonitor.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author mj
* @description 针对表【load_task】的数据库操作Service实现
* @createDate 2025-02-17 22:12:31
*/
@Slf4j
@Service
public class LoadTaskServiceImpl extends ServiceImpl<LoadTaskMapper, LoadTask>
    implements LoadTaskService {

    @Autowired
    private ChaosExerciseRecordsService recordsService;
    
    @Autowired
    private Planv2Service planv2Service;
    
    @Autowired
    private LoadCorrelationService loadCorrelationService;
    
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, Object> getPerformanceMetricsByRecordId(String recordId) {
        // 1. 获取演练记录
        ChaosExerciseRecords record = recordsService.getById(recordId);
        if (record == null) {
            throw new RuntimeException("未找到对应的演练记录");
        }

        // 2. 获取计划信息
        Planv2 plan = planv2Service.getById(record.getPlanId());
        if (plan == null) {
            throw new RuntimeException("未找到对应的计划信息");
        }

        // 3. 检查是否有压测
        String loadId = plan.getLoadId();
        if (!StringUtils.hasText(loadId)) {
            throw new RuntimeException("此次故障注入没有压测");
        }

        // 4. 查询所有关联的压测任务
        List<LoadCorrelation> correlations = loadCorrelationService.lambdaQuery()
                .eq(LoadCorrelation::getChaosExerciseRecordId, recordId)
                .list();
        if (correlations.isEmpty()) {
            throw new RuntimeException("未找到关联的压测任务");
        }

        Map<String, Object> result = new HashMap<>();
        // 5. 处理每个阶段的压测数据
        for (LoadCorrelation correlation : correlations) {
            LoadTask loadTask = this.lambdaQuery()
                    .eq(LoadTask::getTestId, correlation.getLoadTaskId())
                    .one();
            if (loadTask == null) {
                continue;
            }

            String resultPath = loadTask.getResultPath();
            if (!StringUtils.hasText(resultPath)) {
                continue;
            }
            // 下载并解析 CSV 文件
            String csvContent = downloadCsvContent(resultPath);
            String phase = determinePhase(loadTask, correlations); // 传入所有correlations
            log.info("确定压测阶段: {}, taskId: {}", phase, loadTask.getTestId());
            result.put(phase, parseAndCalculateMetrics(csvContent));
        }
        return result;
    }

    private String determinePhase(LoadTask loadTask, List<LoadCorrelation> allCorrelations) {
        // 按创建时间排序所有的loadTask
        List<LoadTask> sortedTasks = allCorrelations.stream()
                .map(correlation -> this.lambdaQuery()
                        .eq(LoadTask::getTestId, correlation.getLoadTaskId())
                        .one())
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(LoadTask::getCreateTime))
                .collect(Collectors.toList());

        if (sortedTasks.size() != 3) {
            log.warn("压测任务数量不等于3，当前数量：{}", sortedTasks.size());
        }

        // 根据当前task的创建时间判断属于哪个阶段
        
        if (sortedTasks.get(0).getTestId().equals(loadTask.getTestId())) {
            return "故障注入前";
        } else if (sortedTasks.get(1).getTestId().equals(loadTask.getTestId())) {
            return "故障注入中";
        } else if (sortedTasks.get(2).getTestId().equals(loadTask.getTestId())) {
            return "故障恢复后";
        } else {
            log.error("无法确定压测任务阶段，taskId: {}", loadTask.getTestId());
            return "未知阶段";
        }
    }

    private Map<String, Object> parseAndCalculateMetrics(String csvContent) {
        try {
            CSVParser parser = CSVParser.parse(new StringReader(csvContent), CSVFormat.DEFAULT.withHeader());
            List<CSVRecord> records = parser.getRecords();
            
            return calculatePhaseMetrics(records);
        } catch (Exception e) {
            log.error("解析压测结果文件失败", e);
            throw new RuntimeException("解析压测结果文件失败: " + e.getMessage());
        }
    }

    private String downloadCsvContent(String resultPath) {
        try {
            return restTemplate.getForObject(resultPath, String.class);
        } catch (Exception e) {
            log.error("下载压测结果文件失败", e);
            throw new RuntimeException("下载压测结果文件失败: " + e.getMessage());
        }
    }

    private Map<String, Object> calculatePhaseMetrics(List<CSVRecord> records) {
        if (records.isEmpty()) {
            return createEmptyPhaseMetrics();
        }

        Map<String, Object> phaseMetrics = new HashMap<>();

        // 计算响应时间分布
        Map<String, Integer> distribution = calculateResponseTimeDistribution(records);
        phaseMetrics.put("responseTimeDistribution", distribution);

        // 计算吞吐量
        double throughput = calculateThroughput(records);
        phaseMetrics.put("throughput", throughput);

        // 计算错误率
        double errorRate = calculateErrorRate(records);
        phaseMetrics.put("errorRate", errorRate);

        // 计算响应时间统计
        Map<String, Object> responseTimeStats = calculateResponseTimeStats(records);
        phaseMetrics.put("responseTimeStats", responseTimeStats);

        // 计算事务吞吐量
        Map<String, Double> transactionThroughput = calculateTransactionThroughput(records);
        phaseMetrics.put("transactionThroughput", transactionThroughput);

        return phaseMetrics;
    }

    private Map<String, Object> createEmptyPhaseMetrics() {
        Map<String, Object> emptyMetrics = new HashMap<>();
        emptyMetrics.put("responseTimeDistribution", new HashMap<String, Integer>());
        emptyMetrics.put("throughput", 0.0);
        emptyMetrics.put("errorRate", 0.0);
        emptyMetrics.put("responseTimeStats", new HashMap<String, Object>());
        emptyMetrics.put("transactionThroughput", new HashMap<String, Double>());
        return emptyMetrics;
    }

    private Map<String, Integer> calculateResponseTimeDistribution(List<CSVRecord> records) {
        Map<String, Integer> distribution = new TreeMap<>();
        distribution.put("0-200ms", 0);
        distribution.put("200-400ms", 0);
        distribution.put("400-600ms", 0);
        distribution.put("600-800ms", 0);
        distribution.put("800ms+", 0);

        for (CSVRecord record : records) {
            long elapsed = Long.parseLong(record.get("elapsed"));
            if (elapsed < 200) distribution.put("0-200ms", distribution.get("0-200ms") + 1);
            else if (elapsed < 400) distribution.put("200-400ms", distribution.get("200-400ms") + 1);
            else if (elapsed < 600) distribution.put("400-600ms", distribution.get("400-600ms") + 1);
            else if (elapsed < 800) distribution.put("600-800ms", distribution.get("600-800ms") + 1);
            else distribution.put("800ms+", distribution.get("800ms+") + 1);
        }

        return distribution;
    }

    private double calculateThroughput(List<CSVRecord> records) {
        if (records.isEmpty()) return 0.0;
        
        long startTime = Long.parseLong(records.get(0).get("timeStamp"));
        long endTime = Long.parseLong(records.get(records.size() - 1).get("timeStamp"));
        double durationInSeconds = (endTime - startTime) / 1000.0;
        
        return durationInSeconds > 0 ? records.size() / durationInSeconds : 0;
    }

    private double calculateErrorRate(List<CSVRecord> records) {
        if (records.isEmpty()) return 0.0;
        
        long errorCount = records.stream()
                .filter(record -> !record.get("success").equals("true"))
                .count();
        
        return (double) errorCount / records.size() * 100;
    }

    private Map<String, Object> calculateResponseTimeStats(List<CSVRecord> records) {
        Map<String, Object> stats = new HashMap<>();
        if (records.isEmpty()) {
            stats.put("min", 0.0);
            stats.put("max", 0.0);
            stats.put("avg", 0.0);
            stats.put("median", 0.0);
            return stats;
        }

        List<Long> elapsedTimes = records.stream()
                .map(record -> Long.parseLong(record.get("elapsed")))
                .collect(Collectors.toList());

        DoubleSummaryStatistics statistics = elapsedTimes.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();

        stats.put("min", statistics.getMin());
        stats.put("max", statistics.getMax());
        stats.put("avg", statistics.getAverage());

        // 计算中位数
        Collections.sort(elapsedTimes);
        double median = elapsedTimes.size() % 2 == 0 ?
                (elapsedTimes.get(elapsedTimes.size()/2-1) + elapsedTimes.get(elapsedTimes.size()/2))/2.0 :
                elapsedTimes.get(elapsedTimes.size()/2);
        stats.put("median", median);

        return stats;
    }

    private Map<String, Double> calculateTransactionThroughput(List<CSVRecord> records) {
        if (records.isEmpty()) return new HashMap<>();

        // 按 label 分组计算吞吐量
        Map<String, List<CSVRecord>> recordsByLabel = records.stream()
                .collect(Collectors.groupingBy(record -> record.get("label")));

        Map<String, Double> throughputByLabel = new HashMap<>();
        
        recordsByLabel.forEach((label, labelRecords) -> {
            double throughput = calculateThroughput(labelRecords);
            throughputByLabel.put(label, throughput);
        });

        return throughputByLabel;
    }
}