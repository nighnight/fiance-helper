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

    // 用户名/昵称只允许中英文、数字、下划线
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_一-龥]+$";

    @Override
    public Result register(SysUser sysUser) {
        // 1. 校验用户名格式（Bug-02 修复：防止注入）
        if (sysUser.getUsername() == null || !sysUser.getUsername().matches(USERNAME_REGEX)) {
            return Result.error("用户名只能包含中英文、数字和下划线");
        }
        // 2. 校验昵称格式
        if (sysUser.getNickname() != null && !sysUser.getNickname().isEmpty()
                && !sysUser.getNickname().matches(USERNAME_REGEX)) {
            return Result.error("昵称只能包含中英文、数字和下划线");
        }

        // 3. 检查用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(sysUser.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 4. 密码加密
        String encodedPassword = passwordEncoder.encode(sysUser.getPassword());
        sysUser.setPassword(encodedPassword);

        // 5. 设置默认昵称（如果没填）
        if (sysUser.getNickname() == null || sysUser.getNickname().isEmpty()) {
            sysUser.setNickname("用户" + System.currentTimeMillis());
        }

        // 6. 插入数据库
        sysUserMapper.insert(sysUser);
        return Result.success("注册成功");
    }

    @Override
    public void updateInfo(SysUser user, HttpSession session) {
        // 1. 更新数据库
        sysUserMapper.update(user);
        // 2. 更新 Session 中的用户信息 (这一步很重要，否则页面右上角不会变)
        SysUser currentUser = sysUserMapper.selectById(user.getId());
        currentUser.setPassword(null); // 安全起见擦除密码
        session.setAttribute("USER_SESSION", currentUser);
    }

    @Override
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        SysUser user = sysUserMapper.selectById(userId);
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        // 加密新密码
        user.setPassword(passwordEncoder.encode(newPwd));
        sysUserMapper.update(user);
    }
}