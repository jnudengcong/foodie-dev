package com.cong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2 {

    // http://localhost:8088/swagger-ui.html 原路径
    // http://localhost:8088/doc.html

    // 配置 swagger2 核心配置 docket
    @Bean
    public Docket createRestAPI() {
        return new Docket(DocumentationType.SWAGGER_2) // 指定 api 类型为 swagger2
                .apiInfo(apiInfo()) // 用于定义 api 文档汇总信息
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.cong.controller")) // 指定 controller 包
                .paths(PathSelectors.any()) // 所有 controller
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("吃呀 电商平台接口api") // 文档页标题
                .contact(new Contact("cong",
                        "https://www.baidu.com",
                        "123@abc.com")) // 联系人信息
                .description("专为吃呀提供的api文档") // 详细信息
                .version("1.0.1") // 文档版本号
                .termsOfServiceUrl("https://www.baidu.com") // 网站地址
                .build();
    }

}
