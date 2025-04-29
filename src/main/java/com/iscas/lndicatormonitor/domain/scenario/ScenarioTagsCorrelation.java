package com.iscas.lndicatormonitor.domain.scenario;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName scenario_tags_correlation
 */
@TableName(value ="scenario_tags_correlation")
@Data
public class ScenarioTagsCorrelation implements Serializable {
    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 
     */
    @TableField(value = "scenario_id")
    private Integer scenarioId;

    /**
     * 
     */
    @TableField(value = "tags_id")
    private Integer tagsId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}