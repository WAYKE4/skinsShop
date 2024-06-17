package com.boot.dbskins;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
        title = "SkinSHOP",
        description = "A skin store written in Spring-Boot",
        contact = @Contact(name = "Maxim Drabysheuski",
                url = "none",
                email = "gitta.tachir@mail.ru"
        )))

@SpringBootApplication
public class DbSkinsApplication {
    public static void main(String[] args) {
        SpringApplication.run(DbSkinsApplication.class, args);
    }
}
