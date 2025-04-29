package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value ="workflowcorrelation")
public class Workflowcorrelation implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name")
    private String name;

    @TableField(value = "workflow_id")
    private Integer workflowId;

    @TableField(value = "record_id")
    private Integer recordId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}