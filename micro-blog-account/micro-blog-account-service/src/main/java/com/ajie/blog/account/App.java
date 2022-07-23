package com.ajie.blog.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author niezhenjie
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.ajie.blog.api.rest")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
