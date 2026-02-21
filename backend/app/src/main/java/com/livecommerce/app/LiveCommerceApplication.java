package com.livecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.livecommerce")
public class LiveCommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveCommerceApplication.class, args);
    }
}
