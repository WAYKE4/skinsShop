package com.boot.dbskins.Security.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class ChangeUserLoginDTO {
    private String oldUserLogin;

    @NotNull
    @Size(min = 3, max = 15)
    private String newUserLogin;
}