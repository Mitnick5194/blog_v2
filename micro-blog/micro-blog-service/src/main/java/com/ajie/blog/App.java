package com.ajie.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.ajie"})
@EnableEurekaClient
//@EnableDiscoveryClient
@EnableFeignClients({"com.ajie.blog.account.api.rest","com.ajie.blog.api.migrate"})
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext cx = SpringApplication.run(App.class);
    }
}