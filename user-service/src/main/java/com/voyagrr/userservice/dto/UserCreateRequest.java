package com.voyagrr.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

        @NotBlank(message = "username is required") @Size(min = 3, max = 50, message = "username must be between 3 and 50 characters") String username,

        @NotBlank(message = "email is required") @Email(message = "invalid email format") String email,

        @NotBlank(message = "first name is required") @Size(max = 50, message = "first name must not exceed 50 characters") String firstName,

        @NotBlank(message = "last name is required") @Size(max = 50, message = "last name must not exceed 50 characters") String lastName,

        @Schema(description = "password must be at least 8 chars, contain uppercase, lowercase, number, and special char") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "password must contain uppercase, lowercase, number, and special character") String password) {
}
