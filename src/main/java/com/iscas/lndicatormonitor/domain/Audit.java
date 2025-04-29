package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
@TableName("audit")
public class Audit implements Serializable{
    /**
    * 审计id
    */
    @TableId
    private Integer id;

    /**
    * 用户名
    */
    @TableField("user_name")
    private String username;

    /**
    * 操作时间
    */
    @TableField("operate_time")
    private Date operateTime;

    /**
    * 操作名称
    */
    @TableField("operate_name")
    private String operateName;

    /**
    * 操作结果
    */
    @TableField("operate_result")
    private Integer operateResult;
}