package com.iscas.lndicatormonitor.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Yukun Hou
 * @create 2023-10-11 15:00
 */

@ApiModel(value = "com-iscas-lndicatormonitor-domain-Users")
@Data
@NoArgsConstructor
public class Users {
    /**
     * 用户唯一id
     */
    @ApiModelProperty(value = "用户唯一id")
    @TableId
    private Integer id;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号")
    private String username;

    /**
     * 用户密码
     */
    @ApiModelProperty(value = "用户密码")
    private String password;

    /**
     * 真实姓名
     */
    @ApiModelProperty(value = "真实姓名")
    private String realName;

}