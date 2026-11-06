package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Данные для входа")
@Getter @Setter
@NoArgsConstructor
public class LoginDto {
    @Schema(description = "Почта", example = "user1@mail.ru")
    @NotBlank @Email
    private String email;

    @Schema(description = "Пароль", example = "password1")
    @NotBlank
    private String password;
}
