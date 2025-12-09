package com.finance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.finance.mapper") // 扫描MyBatis Mapper接口
@EnableScheduling // 开启定时任务，未来可能用于预算提醒等
public class FinanceHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceHelperApplication.class, args);
    }

}