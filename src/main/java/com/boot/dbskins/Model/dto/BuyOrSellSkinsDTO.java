package com.boot.dbskins.Model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class BuyOrSellSkinsDTO {
    @NotNull
    @Positive
    private Long skinId;
    @NotNull
    @Positive
    private Long userId;
}
