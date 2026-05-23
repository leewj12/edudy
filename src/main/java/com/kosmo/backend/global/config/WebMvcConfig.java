package com.kosmo.backend.global.config;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // user.dir 기준으로 upload 폴더 설정 (Docker: /app/upload/, 로컬: ./upload/)
        String uploadPath = "file:" + System.getProperty("user.dir") + "/upload/";
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath);
    }
}

