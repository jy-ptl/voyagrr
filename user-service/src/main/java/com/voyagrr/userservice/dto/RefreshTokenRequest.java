package com.voyagrr.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is mandatory") String refreshToken) {
}
