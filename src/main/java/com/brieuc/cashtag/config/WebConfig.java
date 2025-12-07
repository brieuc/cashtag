package com.brieuc.cashtag.config;

import java.util.Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {  
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/static/","classpath:/images/")
        .setCachePeriod(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*")
        .allowCredentials(false)
        .allowedHeaders("*")
        .allowedMethods("*")
        .allowedOrigins("*");
    }

    @Bean
    @ConditionalOnMissingBean
    public BuildProperties buildProperties() {
        Properties properties = new Properties();
        properties.setProperty("version", "dev");
        return new BuildProperties(properties);
    }
}