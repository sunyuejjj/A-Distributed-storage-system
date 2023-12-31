package com.ksyun.campus.dataserver;

import com.ksyun.campus.dataserver.util.MyStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DataServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataServerApplication.class,args);
    }
}
