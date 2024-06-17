package com.boot.dbskins.Security.Model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class GiveAdminDTO {
    @Positive
    @NotNull
    private Long userId;
}
