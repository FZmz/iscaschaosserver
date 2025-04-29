package com.iscas.lndicatormonitor.domain.reportv2;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName report_v2
 */
@TableName(value ="report_v2")
@Data
public class ReportV2 implements Serializable {
    /**
     * reportv2 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 演练报告名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 演练记录id
     */
    @TableField(value = "chaos_record_id")
    private Integer chaosRecordId;

    /**
     * 创建者
     */
    @TableField(value = "creator")
    private String creator;

    /**
     * 创建时间
     */
    @TableField(value = "creator_time")
    private Date creatorTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 状态(0: 成功 1:失败)
     */
    @TableField(value = "status")
    private String status;

    /**
     * 报告内容
     */
    @TableField(value = "content")
    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}