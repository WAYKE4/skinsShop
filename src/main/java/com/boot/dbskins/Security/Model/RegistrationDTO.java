package com.boot.dbskins.Security.Model;

import com.boot.dbskins.Annotation.IsAdult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class RegistrationDTO {

    @NotNull
    @Size(min = 3, max = 10)
    private String login;

    @NotNull
    @Size(min = 5)
    private String password;

    @NotNull
    @Size(min = 3, max = 10)
    private String username;

    @NotNull
    @IsAdult
    private Integer age;

    @NotNull
    @Email
    private String email;
}
