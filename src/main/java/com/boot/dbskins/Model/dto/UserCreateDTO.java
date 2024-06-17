package com.boot.dbskins.Model.dto;

import com.boot.dbskins.Annotation.IsAdult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserCreateDTO {

    @NotNull
    @Size(min = 6, max = 15)
    private String username;

    @Positive
    @NotNull
    @IsAdult
    private Integer age;

    @NotNull
    @Size(min = 3, max = 20)
    @Email
    private String email;

    @NotNull
    @Size(min = 5)
    private String userPassword;
}
