package com.shop.generator.dto;

import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter @Getter @ToString @AllArgsConstructor @NoArgsConstructor
public class ProductEventDto {
    UUID eventId;
    UUID productId;
    BigDecimal newPrice;
    Integer newAvailableStock;
    LocalDateTime eventTime;
}
