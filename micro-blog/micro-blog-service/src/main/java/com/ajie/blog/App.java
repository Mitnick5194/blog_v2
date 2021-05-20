package com.ajie.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.ajie"})
@EnableEurekaClient
@EnableFeignClients("com.ajie.blog.account.api.rest")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}