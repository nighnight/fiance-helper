package com.finance.service.impl;

import com.finance.mapper.SysUserMapper;
import com.finance.po.SysUser;
import com.finance.service.SysUserService;
import com.finance.util.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SysUserServiceImplTest {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @Test
    @DisplayName("USER-01: 正常注册")
    void registerSuccess() {
        SysUser user = new SysUser();
        user.setUsername("zhangsan");
        user.setNickname("张三");
        user.setPassword("123456");

        Result result = userService.register(user);

        assertThat(result.getCode()).isEqualTo(200);

        SysUser dbUser = userMapper.selectByUsername("zhangsan");
        assertThat(dbUser).isNotNull();
        assertThat(dbUser.getPassword()).isNotEqualTo("123456"); // 已加密
    }

    @Test
    @DisplayName("USER-02: 重复用户名注册失败")
    void registerDuplicateUsername() {
        SysUser user1 = new SysUser();
        user1.setUsername("lisi");
        user1.setNickname("李四");
        user1.setPassword("123456");
        userService.register(user1);

        SysUser user2 = new SysUser();
        user2.setUsername("lisi");
        user2.setNickname("李四2");
        user2.setPassword("654321");

        Result result = userService.register(user2);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMessage()).contains("已存在");
    }

    @Test
    @DisplayName("USER-03: 注册含表情符号应失败")
    void registerWithEmojiShouldFail() {
        SysUser user = new SysUser();
        user.setUsername("user😀");
        user.setNickname("昵称");
        user.setPassword("123456");

        Result result = userService.register(user);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMessage()).contains("只能包含");
    }

    @Test
    @DisplayName("USER-04: 注册含HTML标签应失败")
    void registerWithHtmlShouldFail() {
        SysUser user = new SysUser();
        user.setUsername("<script>alert(1)</script>");
        user.setNickname("昵称");
        user.setPassword("123456");

        Result result = userService.register(user);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMessage()).contains("只能包含");
    }

    @Test
    @DisplayName("USER-05: 正常登录")
    void loginSuccess() {
        SysUser user = new SysUser();
        user.setUsername("wangwu");
        user.setNickname("王五");
        user.setPassword("123456");
        userService.register(user);

        Result result = userService.login("wangwu", "123456", session);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(session.getAttribute("USER_SESSION")).isNotNull();
    }

    @Test
    @DisplayName("USER-06: 登录密码错误")
    void loginWrongPassword() {
        SysUser user = new SysUser();
        user.setUsername("zhaoliu");
        user.setNickname("赵六");
        user.setPassword("123456");
        userService.register(user);

        Result result = userService.login("zhaoliu", "wrongpassword", session);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMessage()).contains("密码错误");
    }

    @Test
    @DisplayName("USER-07: 登录不存在用户")
    void loginUserNotFound() {
        Result result = userService.login("notexist", "123456", session);

        assertThat(result.getCode()).isNotEqualTo(200);
        assertThat(result.getMessage()).contains("不存在");
    }

    @Test
    @DisplayName("USER-08: 修改密码成功")
    void updatePasswordSuccess() {
        SysUser user = new SysUser();
        user.setUsername("sunqi");
        user.setNickname("孙七");
        user.setPassword("123456");
        userService.register(user);

        Long userId = userMapper.selectByUsername("sunqi").getId();

        userService.updatePassword(userId, "123456", "newpassword");

        Result result = userService.login("sunqi", "newpassword", session);
        assertThat(result.getCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("USER-09: 修改密码失败（旧密码错误）")
    void updatePasswordWithWrongOldPwd() {
        SysUser user = new SysUser();
        user.setUsername("zhouba");
        user.setNickname("周八");
        user.setPassword("123456");
        userService.register(user);

        Long userId = userMapper.selectByUsername("zhouba").getId();

        assertThatThrownBy(() -> userService.updatePassword(userId, "wrong", "newpassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("原密码错误");
    }
}
