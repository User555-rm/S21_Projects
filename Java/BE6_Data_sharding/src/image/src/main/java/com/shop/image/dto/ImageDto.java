package com.shop.image.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ImageDto {
    UUID id;
}
