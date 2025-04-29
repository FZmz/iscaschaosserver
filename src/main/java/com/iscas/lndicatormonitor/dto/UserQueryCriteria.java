package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class UserQueryCriteria {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String username;  // 用户名，支持模糊查询
    private String realName;  // 真实姓名，支持模糊查询
    private Integer roleType; // 用户角色类型
}