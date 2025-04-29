package com.iscas.lndicatormonitor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
public class UsersDTO {
    private int id;
    private String username;
    private String password;
    private String real_name;
    private int role;
    private String captcha;

    // 使用 @JsonIgnore 注解忽略这个字段的反序列化
    @JsonIgnore
    private List<GrantedAuthority> authorities;
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将整型 role 转换为字符串
        return Collections.singletonList(new SimpleGrantedAuthority(String.valueOf(role)));
    }

}
