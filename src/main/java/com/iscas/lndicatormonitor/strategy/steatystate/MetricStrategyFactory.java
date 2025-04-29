package com.iscas.lndicatormonitor.strategy.steatystate;

public class MetricStrategyFactory {

    private static final IMetricStrategy METRIC_STRATEGY = new SteadyStateMetricStrategy();

    /**
     * 返回统一的 SteadyStateMetricStrategy 实例
     *
     * @param type 稳态类型（当前参数保留兼容性，实际不再使用）
     * @return 统一的稳态策略实例
     */
    public static IMetricStrategy getStrategy(Integer type) {
        if (type == null) {
            throw new IllegalArgumentException("SteadyStateRepository.type 不能为空");
        }

        // 此处已不需要根据 type 返回不同策略，返回统一实例即可
        return METRIC_STRATEGY;
    }
}