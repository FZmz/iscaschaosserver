package com.iscas.lndicatormonitor.dto;

import lombok.Data;

@Data
public class LoginRes {
    private UsersDTO usersDTO;
    private String token;
}
