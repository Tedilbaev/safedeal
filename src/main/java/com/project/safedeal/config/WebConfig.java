package com.project.safedeal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userDataPath = new File("../userData").getAbsolutePath() + "/";
        System.out.println("Путь к userData: " + userDataPath); // Для отладки
        registry.addResourceHandler("/userData/**")
                .addResourceLocations("file:" + userDataPath);
    }
}