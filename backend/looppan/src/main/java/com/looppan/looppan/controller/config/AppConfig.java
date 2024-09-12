package com.looppan.looppan.controller.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    @Value("${spring.mail.username:}")
    private String emailUsername;

}
