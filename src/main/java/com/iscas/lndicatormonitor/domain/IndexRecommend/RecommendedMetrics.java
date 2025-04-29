package com.iscas.lndicatormonitor.domain.IndexRecommend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName recommended_metrics
 */
@TableName(value ="recommended_metrics")
@Data
public class RecommendedMetrics implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 推荐指标名称
     */
    private String metricName;

    /**
     * 处理器工具（如 Prometheus）
     */
    private String tool;

    /**
     * 
     */
    private String operationPath;

    private String description;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}