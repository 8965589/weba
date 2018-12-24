package com.ander.weba;

import com.ander.weba.config.AccessConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AccessConfig.class})
public class WebaServer {

    public static void main(String[] args) {
        System.out.println("test");
        SpringApplication.run(WebaServer.class, args);
    }
}
