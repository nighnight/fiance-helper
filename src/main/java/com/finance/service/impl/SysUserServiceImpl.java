package com.finance.service.impl;

import com.finance.mapper.SysUserMapper;
import com.finance.po.SysUser;
import com.finance.service.SysUserService;
import com.finance.util.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    // 使用 BCrypt 进行密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Result login(String username, String password, HttpSession session) {
        // 1. 查询用户
        SysUser user = sysUserMapper.selectByUsername(username);

        // 2. 校验用户是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 3. 校验密码 (数据库存的是密文，需要用 matches 方法比对)
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }

        // 4. 登录成功，存入 Session
        // 注意：生产环境不要把密码存入Session，这里清空一下
        user.setPassword(null);
        session.setAttribute("USER_SESSION", user);

        return Result.success("登录成功");
    }

    @Override
    public Result register(SysUser sysUser) {
        // 1. 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(sysUser.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 2. 密码加密
        String encodedPassword = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setPassword(encodedPassword);

        // 3. 设置默认昵称（如果没填）
        if (sysUser.getNickname() == null || sysUser.getNickname().isEmpty()) {
            sysUser.setNickname("用户" + System.currentTimeMillis());
        }

        // 4. 插入数据库
        sysUserMapper.insert(sysUser);
        return Result.success("注册成功");
    }
}