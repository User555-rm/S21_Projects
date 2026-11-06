package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Информация о товаре")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ProductDto {
    UUID id;
    @NotBlank(message = "Введите имя")
    String name;
    CategoryDto category;
    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    BigDecimal price;
    @NotNull(message = "Укажите количество")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    Integer availableStock;
    @PastOrPresent(message = "Дата прихода товара должна быть не позднее текущей даты")
    LocalDateTime lastUpdateDate;
    SupplierDto supplier;
    ImageDto image;
}
