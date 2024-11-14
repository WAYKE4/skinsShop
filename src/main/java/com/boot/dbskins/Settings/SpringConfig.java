package com.boot.dbskins.Settings;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@ComponentScan("com.boot.dbskins")
@Configuration
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT", scheme = "bearer")
public class SpringConfig implements WebMvcConfigurer {

}
