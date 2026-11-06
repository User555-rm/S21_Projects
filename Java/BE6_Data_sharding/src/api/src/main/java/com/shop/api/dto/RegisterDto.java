package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Schema(description = "Данные для регистрации")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto {
    @Schema(description = "Почта", example = "user1@mail.ru")
    @NotBlank(message = "Введите корректный email")
    @Email
    String email;
    @Schema(description = "Имя", example = "User1")
    @NotBlank(message = "Введите имя")
    String name;
    @Schema(description = "Фамилия", example = "UserSurname1")
    @NotBlank(message = "Введите фамилию")
    String surname;
    @Schema(description = "Номер телефона", example = "8-800-223-3322")
    String phoneNumber;
    @Schema(description = "Пароль", example = "password1")
    @NotBlank(message = "Введите пароль")
    String password;
}
