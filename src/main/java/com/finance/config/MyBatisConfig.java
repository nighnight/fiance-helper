package com.finance.config;

import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class MyBatisConfig {

    @Bean
    @Primary
    public ConfigurationCustomizer configurationCustomizer() {
        return new ConfigurationCustomizer() {
            @Override
            public void customize(Configuration configuration) {
                // 开启驼峰命名自动映射，如 create_time -> createTime
                configuration.setMapUnderscoreToCamelCase(true);
                // 配置日志输出，如果需要调试SQL，可以设置为STDOUT_LOGGING
                // configuration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
            }
        };
    }
}