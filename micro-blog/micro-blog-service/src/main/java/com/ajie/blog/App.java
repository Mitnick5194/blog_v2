package com.ajie.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.ajie"})
//@EnableEurekaClient
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}