package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName loadScript
 */
@TableName(value ="load_script")
@Data
public class LoadScript implements Serializable {
    /**
     * 负载脚本表
     */
    @TableId
    private String id;

    /**
     * 负载id
     */
    private String loadId;

    /**
     * 负载脚本地址
     */
    private String loadScriptUrl;

    /**
     * 创建时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}