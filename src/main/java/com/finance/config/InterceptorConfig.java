package com.finance.config;

import com.finance.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 替代@Resource的方案：构造器注入（最稳定）
@Configuration
@RequiredArgsConstructor // Lombok自动生成构造器，注入LoginInterceptor
public class InterceptorConfig implements WebMvcConfigurer {

    // private final 保证注入的对象非null
    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/static/**",
                        "/api/v1/user/toLogin",
                        "/api/v1/user/toRegister",
                        "/api/v1/user/login",
                        "/api/v1/user/register",
                        "/user/login"
                );
    }
}