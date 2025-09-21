package com.voyagrr.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(

        @NotBlank(message = "username is required") @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters") String username,

        @Schema(description = "password must be at least 8 chars, contain uppercase, lowercase, number, and special char") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "password must contain uppercase, lowercase, number, and special character") String password) {
}
