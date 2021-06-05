package com.ajie.blog;

import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.migrate.MigrateRestApi;
import com.ajie.commons.RestResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.ajie"})
@EnableEurekaClient
//@EnableDiscoveryClient
@EnableFeignClients({"com.ajie.blog.account.api.rest","com.ajie.blog.api.migrate"})
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext cx = SpringApplication.run(App.class);
       /* MigrateRestApi rest = cx.getBean(MigrateRestApi.class);
        RestResponse<List<BlogRespDto>> data = rest.loadBlog();
        System.out.println(data.getData());*/
    }
}