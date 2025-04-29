package com.iscas.lndicatormonitor.domain;

import java.util.Date;
import lombok.Data;

@Data
public class Loginattempt {
    private Integer id;

    private Integer userId;

    /**
    * 错误登录次数
    */
    private Integer loginAttempts;

    /**
    * 锁定期限
    */
    private Date lockUntil;
}