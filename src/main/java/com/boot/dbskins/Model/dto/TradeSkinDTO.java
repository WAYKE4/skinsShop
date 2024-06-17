package com.boot.dbskins.Model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TradeSkinDTO {
    @NotNull
    @Positive
    private Long skinIdFrom;
    @NotNull
    @Positive
    private Long userIdTo;
    @NotNull
    @Positive
    private Long skinIdTo;
}
