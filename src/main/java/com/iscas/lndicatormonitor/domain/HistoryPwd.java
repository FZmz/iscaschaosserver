package com.iscas.lndicatormonitor.domain;

import java.util.Date;
import lombok.Data;

@Data
public class HistoryPwd {
    private Integer id;

    /**
    * 用户ID
    */
    private Integer userid;

    private String passwordhash;

    /**
    * 改变日期
    */
    private Date changedate;
}