package com.finance.service;

import com.finance.dto.UserLoginDTO;
import com.finance.dto.UserRegisterDTO;
import com.finance.po.SysUser;
import com.finance.vo.LoginUserVO;

public interface SysUserService {
    LoginUserVO login(UserLoginDTO userLoginDTO);
    void register(UserRegisterDTO userRegisterDTO);
    SysUser getUserById(Long userId);
    void updateUserInfo(SysUser user);
    void updatePassword(Long userId, String oldPassword, String newPassword);
    void logout(Long userId); // 如果有token管理或session清理逻辑
}