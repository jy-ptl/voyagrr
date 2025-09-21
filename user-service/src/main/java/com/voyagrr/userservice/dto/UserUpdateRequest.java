package com.voyagrr.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(

        @NotBlank(message = "first name is required") @Size(max = 50, message = "first name must not exceed 50 characters") String firstName,

        @NotBlank(message = "last name is required") @Size(max = 50, message = "last name must not exceed 50 characters") String lastName

) {
}
