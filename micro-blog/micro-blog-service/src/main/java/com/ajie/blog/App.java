package com.ajie.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author niezhenjie
 */
@SpringBootApplication(scanBasePackages = {"com.ajie"})
@EnableDiscoveryClient
@EnableFeignClients({"com.ajie.blog.account.api.rest","com.ajie.blog.api.migrate"})
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext cx = SpringApplication.run(App.class);
    }
}
