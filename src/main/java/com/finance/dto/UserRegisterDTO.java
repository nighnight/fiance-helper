package com.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名须由4到16位（字母，数字，下划线，减号）组成")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d!@#$%^&*()_+-=]{8,18}$", message = "密码必须包含大小写字母和数字，长度在8-18位之间")
    private String password;
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}