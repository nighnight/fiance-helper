package com.finance.service;

import com.finance.po.SysUser;
import com.finance.util.Result;
import jakarta.servlet.http.HttpSession;

public interface SysUserService {
    Result login(String username, String password, HttpSession session);
    Result register(SysUser sysUser);
}