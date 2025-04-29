package com.iscas.lndicatormonitor.dto.load;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iscas.lndicatormonitor.domain.LoadApi;
import com.iscas.lndicatormonitor.domain.LoadScript;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadDTO implements Serializable {
    /**
     * 负载id
     */
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 负载名称
     */
    private String name;

    /**
     * 负载类型(0: script 1: api)
     */
    private Integer type;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 负载API列表
     */
    private List<LoadApi> loadApiList;

    /**
     * 负载脚本列表
     */
    private List<LoadScript> loadScriptList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    // Getters and Setters
}
