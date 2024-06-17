package com.boot.dbskins.Security.Model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AuthRequestDTO {
    private String login;
    private String password;
}