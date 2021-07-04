package com.ajie.blog.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
//@EnableDiscoveryClient
@EnableFeignClients("com.ajie.blog.api.rest")
public class App {
    static InheritableThreadLocal<String> tl = new InheritableThreadLocal<>();

    public static void main(String[] args) {
        SpringApplication.run(App.class);
       /* tl.set("aa");
        new Thread(() -> {
            System.out.println(tl.get());
        }).start();*/
    }
}
