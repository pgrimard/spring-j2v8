package com.patrickgrimard.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringJ2V8Application {

    @Bean
    public V8ScriptTemplateViewResolver viewResolver() {
        return new V8ScriptTemplateViewResolver("/static/", ".html");
    }

    @Bean
    public V8ScriptTemplateConfigurer v8ScriptTemplateConfigurer() {
        return new V8ScriptTemplateConfigurer("static/server.js");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringJ2V8Application.class, args);
    }
}
