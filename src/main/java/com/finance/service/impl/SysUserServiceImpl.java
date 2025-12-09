package com.finance.service.impl;

import com.finance.dto.UserLoginDTO;
import com.finance.dto.UserRegisterDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.SysUserMapper;
import com.finance.po.SysUser;
import com.finance.service.SysUserService;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 引入Spring Security的PasswordEncoder
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder; // 注入密码编码器
    @Autowired
    private HttpSession session; // 注入HttpSession

    @Override
    public LoginUserVO login(UserLoginDTO userLoginDTO) {
        SysUser user = sysUserMapper.selectByUsername(userLoginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用，请联系管理员");
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        // 将登录用户信息存储到session
        session.setAttribute("loginUser", loginUserVO);
        return loginUserVO;
    }

    @Override
    public void register(UserRegisterDTO userRegisterDTO) {
        // 1. 检查密码和确认密码是否一致
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 检查用户名是否已存在
        SysUser existingUser = sysUserMapper.selectByUsername(userRegisterDTO.getUsername());
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 3. 构建用户PO并保存
        SysUser newUser = new SysUser();
        newUser.setUsername(userRegisterDTO.getUsername());
        // newUser.setPassword(MD5Util.md5(userRegisterDTO.getPassword())); // 使用BCrypt替代
        newUser.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword())); // 密码加密
        newUser.setNickname(userRegisterDTO.getUsername()); // 默认昵称与用户名相同
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());
        newUser.setStatus(1); // 默认正常状态

        int result = sysUserMapper.insert(newUser);
        if (result != 1) {
            throw new BusinessException("注册失败，请稍后再试");
        }
    }

    @Override
    public SysUser getUserById(Long userId) {
        return sysUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(SysUser user) {
        SysUser existingUser = sysUserMapper.selectByPrimaryKey(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        existingUser.setNickname(user.getNickname());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setUpdateTime(LocalDateTime.now());
        // 用户名不允许通过此接口修改
        sysUserMapper.updateByPrimaryKeySelective(existingUser);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码不正确");
        }
        // 确保新密码符合规则，这里可以再次校验DTO中的Pattern
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void logout(Long userId) {
        // 清理session中的用户数据
        if (session != null) {
            session.removeAttribute("loginUser");
            session.invalidate(); // 使整个session失效
        }
    }
}