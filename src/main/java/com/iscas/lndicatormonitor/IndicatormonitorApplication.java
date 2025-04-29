package com.iscas.lndicatormonitor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.iscas.lndicatormonitor.mapper")
public class IndicatormonitorApplication {
    public static void main(String[] args) {
        SpringApplication.run(IndicatormonitorApplication.class, args);
    }
}