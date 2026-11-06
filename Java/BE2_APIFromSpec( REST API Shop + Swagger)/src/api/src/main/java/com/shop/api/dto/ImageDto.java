package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Schema(description = "Изображение товара")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ImageDto {
    UUID id;
    byte[] image;
    ProductDto product;
}
