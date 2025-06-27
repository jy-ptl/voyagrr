package com.voyagrr.userservice.dto;

public record UserLoginRequest(
        String username,
        String password
) {
}
