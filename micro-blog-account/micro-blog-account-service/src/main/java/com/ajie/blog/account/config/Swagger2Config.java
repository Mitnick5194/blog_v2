package com.ajie.blog.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2 接口配置
 */

@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class Swagger2Config {
    /**
     * 添加摘要信息(Docket)
     */
    @Bean
    public Docket controllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("博客-用户系统_v2接口文档")
                        .description("描述：博客-用户接口文档.")
                        .version("版本号:v_1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ajie.blog.account.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}