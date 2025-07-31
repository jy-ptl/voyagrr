package com.voyagrr.userservice.dto;

public record UserCreateRequest(
        String username,
        String email,
        String firstName,
        String lastName,
        String password) {
}
