package com.iscas.lndicatormonitor.strategy.steatystate;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface IMetricStrategy {

    /**
     * 根据稳态类型和名称计算具体值
     *
     * @param type 稳态类型（例如：全局稳态、局部稳态）
     * @param steadyStateName 稳态名称（例如：P50, TPS 等）
     * @param rootNode Coroot API 返回的 JSON 根节点
     * @return 稳态值
     */
    Object compute(int type, String steadyStateName, JsonNode rootNode);
}