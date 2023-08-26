package com.ksyun.campus.dataserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CommonConfig commonConfig() {
        return new CommonConfig();
    }
}