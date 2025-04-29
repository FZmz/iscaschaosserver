package com.iscas.lndicatormonitor.dto.steadystate;

import lombok.Data;

/**
 * 数据传输对象: 稳态结果
 */
@Data
public class SteadyResultDTO {


    /**
     * 期望值
     */
    private Object expectedValue;

    /**
     * 实际值
     */
    private Object actualValue;

    /**
     * 稳态单位
     */
    private String steadyUnit;

    /**
     * 比较符
     */
    private String comparator;
    /**
     * 是否满足稳态要求
     */
    private Boolean isSteadySatisfied;
}