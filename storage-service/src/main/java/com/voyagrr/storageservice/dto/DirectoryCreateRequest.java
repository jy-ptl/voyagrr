package com.voyagrr.storageservice.dto;

public record DirectoryCreateRequest(
        Long parentDirectoryId,
        String name) {
}
