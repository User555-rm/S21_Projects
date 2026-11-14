package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Schema(description = "Информация о поставщике")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class SupplierDto {
    UUID id;
    @NotBlank(message = "Введите название поставщика")
    String name;
    AddressDto address;
    String phoneNumber;
}
