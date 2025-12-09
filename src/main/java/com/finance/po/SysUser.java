package com.finance.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password; // 存储加密后的密码
    private String nickname;
    private String phone;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status; // 1-正常，0-禁用
}