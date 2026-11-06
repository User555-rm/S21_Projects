package com.shop.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Данные для смены пароля")
@Getter
@Setter
@NoArgsConstructor
public class ChangePassDto {
    @Schema(description = "Новый пароль", example = "New_password1")
    @NotBlank
    private String newPassword;
}
