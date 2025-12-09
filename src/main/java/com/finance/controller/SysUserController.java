package com.finance.controller;

import com.finance.dto.UserLoginDTO;
import com.finance.dto.UserRegisterDTO;
import com.finance.exception.BusinessException;
import com.finance.po.SysUser;
import com.finance.service.SysUserService;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private HttpSession session; // 直接注入session

    /**
     * 跳转登录页面 (由WebConfig配置)
     * @return "user/login"
     */
    // @GetMapping("/login")
    // public String loginPage() {
    //     return "user/login";
    // }

    /**
     * 跳转注册页面 (由WebConfig配置)
     * @return "user/register"
     */
    // @GetMapping("/register")
    // public String registerPage() {
    //     return "user/register";
    // }

    /**
     * 用户登录API
     * @param userLoginDTO 登录DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/login")
    @ResponseBody
    public Result<LoginUserVO> login(@Valid @RequestBody UserLoginDTO userLoginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        LoginUserVO loginUserVO = sysUserService.login(userLoginDTO);
        // 登录成功后会自动将LoginUserVO存入session
        return ResultUtil.success("登录成功", loginUserVO);
    }

    /**
     * 用户注册API
     * @param userRegisterDTO 注册DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/register")
    @ResponseBody
    public Result<String> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        sysUserService.register(userRegisterDTO);
        return ResultUtil.success("注册成功");
    }

    /**
     * 用户登出API
     * @return JSON结果
     */
    @GetMapping("/logout")
    public String logout() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser != null) {
            sysUserService.logout(loginUser.getId());
        }
        return "redirect:/user/login"; // 重定向到登录页面
    }

    /**
     * 用户信息页面
     * @param model Model
     * @return "user/info"
     */
    @GetMapping("/info")
    public String userInfo(Model model) {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录"); // 理论上会被拦截器拦截
        }
        SysUser user = sysUserService.getUserById(loginUser.getId());
        model.addAttribute("user", user);
        return "user/info";
    }

    /**
     * 更新用户信息API
     * @param sysUser 用户PO (只更新nickname, phone, email)
     * @return JSON结果
     */
    @PostMapping("/api/updateInfo")
    @ResponseBody
    public Result<String> updateUserInfo(@RequestBody SysUser sysUser) {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            return ResultUtil.error(401, "用户未登录");
        }
        sysUser.setId(loginUser.getId()); // 确保只修改当前用户
        sysUserService.updateUserInfo(sysUser);

        // 更新session中的nickname
        loginUser.setNickname(sysUser.getNickname());
        session.setAttribute("loginUser", loginUser);

        return ResultUtil.success("用户信息更新成功");
    }

    /**
     * 跳转修改密码页面
     * @return "user/changePwd"
     */
    @GetMapping("/changePwd")
    public String changePasswordPage() {
        return "user/changePwd";
    }

    /**
     * 修改密码API
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param confirmNewPassword 确认新密码
     * @return JSON结果
     */
    @PostMapping("/api/changePassword")
    @ResponseBody
    public Result<String> changePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword,
                                         @RequestParam String confirmNewPassword) {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            return ResultUtil.error(401, "用户未登录");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            return ResultUtil.error(400, "两次输入的新密码不一致");
        }
        // 简单密码复杂度校验，实际应更严格或使用DTO进行参数校验
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+-=]{8,18}$")) {
            return ResultUtil.error(400, "新密码必须包含大小写字母和数字，长度在8-18位之间");
        }

        sysUserService.updatePassword(loginUser.getId(), oldPassword, newPassword);
        return ResultUtil.success("密码修改成功，请重新登录"); // 建议修改密码后强制用户重新登录
    }
}