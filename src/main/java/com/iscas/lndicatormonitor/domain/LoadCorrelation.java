package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * @TableName load_correlation
 */
@TableName(value ="load_correlation")
@Data
public class LoadCorrelation implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 测试任务id
     */
    @TableField(value = "load_task_id")
    private String loadTaskId;

    /**
     * 故障演练记录id
     */
    @TableField(value = "chaos_exercise_record_id")
    private Integer chaosExerciseRecordId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}