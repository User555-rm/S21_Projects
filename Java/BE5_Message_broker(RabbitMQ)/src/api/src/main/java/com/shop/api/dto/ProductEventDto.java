package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Изменения товара по событию")
@Setter @Getter @ToString @AllArgsConstructor @NoArgsConstructor
public class ProductEventDto {
    UUID eventId;
    UUID productId;
    BigDecimal newPrice;
    Integer newAvailableStock;
    LocalDateTime eventTime;
}
