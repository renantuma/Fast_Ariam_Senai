package com.fastariam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FastAriamApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastAriamApplication.class, args);
    }
}
