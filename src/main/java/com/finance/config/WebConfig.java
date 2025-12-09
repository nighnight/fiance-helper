package com.finance.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // === 业务放行 ===
                        "/user/login",
                        "/user/register",
                        "/user/doLogin",
                        "/user/doRegister",
                        "/error",

                        // === 静态资源放行 ===
                        "/css/**",
                        "/js/**",
                        "/lib/**",
                        "/images/**",
                        "/favicon.ico",

                        // === Swagger/Knif4j 放行 (关键) ===
                        "/doc.html",              // 如果用Knif4j
                        "/swagger-ui/**",         // Swagger UI 静态资源
                        "/swagger-ui.html",       // Swagger UI 入口
                        "/v3/api-docs/**"         // OpenAPI 描述 JSON
                );
    }
}