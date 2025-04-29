package com.iscas.lndicatormonitor.domain;

import lombok.Data;

@Data
public class Role {
    /**
    * j角色id
    */
    private Integer id;

    /**
    * 用户id
    */
    private Integer userid;

    /**
    * 角色类型
    * 1: 用户管理员 - 只有用户管理权限
    * 2: 审计员 - 只有审计日志权限
    * 3: 操作员 - 拥有除审计日志、用户管理外的所有权限
    * 4: 超级管理员 - 拥有所有权限
    */
    private Integer roleType;
    
    // 角色类型常量
    public static final int ROLE_ADMIN = 1;       // 用户管理员
    public static final int ROLE_AUDITOR = 2;     // 审计员
    public static final int ROLE_OPERATOR = 3;    // 操作员
    public static final int ROLE_SUPER_ADMIN = 4; // 超级管理员
}