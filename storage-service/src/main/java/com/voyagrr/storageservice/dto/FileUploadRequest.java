package com.voyagrr.storageservice.dto;

public record FileUploadRequest(
        String name,
        Long directoryId
) {
}
