package com.boot.dbskins.Security.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ChangeEmailDTO {
    private String login;

    @NotNull
    @Email
    private String newEmail;
}