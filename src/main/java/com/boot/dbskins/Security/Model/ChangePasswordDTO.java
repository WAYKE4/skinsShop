package com.boot.dbskins.Security.Model;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ChangePasswordDTO {
    private String login;
    private String oldPassword;

    @NotNull
    @Size(min = 3, max = 15)
    private String newPassword;
}