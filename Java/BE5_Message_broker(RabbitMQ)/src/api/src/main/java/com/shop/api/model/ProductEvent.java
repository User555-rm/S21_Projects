package com.shop.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_events")
@Getter @Setter @NoArgsConstructor
public class ProductEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    @Column(name = "product_id", nullable = false)
    UUID productId;
    @Column(name = "new_price")
    BigDecimal newPrice;
    @Column(name = "new_available_stock")
    Integer newAvailableStock;
    @Column(name = "event_time")
    LocalDateTime eventTime;
}
