package com.finance.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "系统用户实体")
public class SysUser {

    @Schema(description = "用户ID", hidden = true) // hidden=true 表示在请求示例中隐藏
    private Long id;

    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "昵称", example = "财务小助手")
    private String nickname;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "创建时间", hidden = true)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", hidden = true)
    private LocalDateTime updateTime;

    @Schema(description = "状态 1:正常 0:禁用", hidden = true)
    private Integer status;

    // ... 其他字段 ...
    @Schema(description = "头像URL")
    private String avatar;
// ...
}