package com.livecommerce.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = "com.livecommerce")
@EnableJpaRepositories(basePackages = "com.livecommerce")
@SpringBootApplication(scanBasePackages = "com.livecommerce")
public class LiveCommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveCommerceApplication.class, args);
    }
}
