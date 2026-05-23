package com.kosmo.backend.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 현재 프로젝트 루트 경로를 기준으로 upload 폴더 경로 설정
//        String uploadPath = Paths.get("").toAbsolutePath().toString() + "/upload/";
//
//        registry.addResourceHandler("/upload/**")
//                .addResourceLocations("file:" + uploadPath);
//    }
//}


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ServletContext servletContext;

    private String uploadPath;

    @PostConstruct
    public void init() {
        // 웹 애플리케이션의 루트 실제 경로 기준으로 upload 폴더 설정
        String realPath = servletContext.getRealPath("/upload/");
        uploadPath = "file:" + realPath;
        System.out.println(">>> Upload path: " + uploadPath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}

