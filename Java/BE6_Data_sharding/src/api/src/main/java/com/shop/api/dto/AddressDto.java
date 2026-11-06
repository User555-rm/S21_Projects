package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Schema(description = "Адреса клиентов поставщиков")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class AddressDto {
    UUID id;
    String country;
    String city;
    String street;
}
