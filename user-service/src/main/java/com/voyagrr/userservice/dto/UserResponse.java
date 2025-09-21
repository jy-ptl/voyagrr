package com.voyagrr.userservice.dto;

public record UserResponse(
        String username,
        String firstName,
        String lastName,
        String email) {
}
