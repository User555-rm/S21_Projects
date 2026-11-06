package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Информация о клиенте")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ClientDto {
    UUID id;
    @NotBlank(message = "Введите имя")
    String clientName;
    @NotBlank(message = "Введите Фамилию")
    String clientSurname;
    @Past(message = "Дата рождения должна быть меньше текущей даты")
    LocalDate birthday;
    @NotBlank(message = "Введите пол")
    String gender;
    @PastOrPresent(message = "Дата регистрации клиента должна быть не позднее текущей даты")
    LocalDateTime registrationDate;
    AddressDto address;
}
