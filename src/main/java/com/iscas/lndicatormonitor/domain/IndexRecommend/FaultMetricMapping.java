package com.iscas.lndicatormonitor.domain.IndexRecommend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName fault_metric_mapping
 */
@TableName(value ="fault_metric_mapping")
@Data
public class FaultMetricMapping implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 故障id
     */
    private Integer faultId;

    /**
     * 指标id
     */
    private Integer metricId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}