package com.voyagrr.userservice.dto;

public record UserSearchResponse(
        String keycloakUserId,
        String username,
        String firstName,
        String lastName,
        String email) {
}
