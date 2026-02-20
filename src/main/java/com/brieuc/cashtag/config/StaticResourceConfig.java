package com.brieuc.cashtag.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev")
public class StaticResourceConfig implements WebMvcConfigurer {
    
    @Value("${app.uploads.path}")
    private String uploadsPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadsPath + "/");
    }
}