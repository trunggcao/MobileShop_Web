package com.example.Mobile.Ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:upload/");
        registry
                .addResourceHandler("/client/**")
                .addResourceLocations("classpath:/static/client/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Mapping cho thư mục js
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");

        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");

        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/favicon.ico");

        //lay hinh anh tu product & avatar user
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:upload/images/");



    }
}