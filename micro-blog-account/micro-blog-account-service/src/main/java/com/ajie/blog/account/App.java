package com.ajie.blog.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients("com.ajie.blog.api.rest")
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext cx = SpringApplication.run(App.class);
        StringRedisTemplate temp = cx.getBean("stringRedisTemplate", StringRedisTemplate.class);
        //temp.opsForValue().set("test","hello redis");
        temp.opsForValue().set("test", "1");
        String test = temp.opsForValue().get("test");
        System.out.println(test);
        temp.opsForValue().increment("test1");
        test = temp.opsForValue().get("test1");
        System.out.println(test);

    }
}
