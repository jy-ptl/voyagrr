package com.voyagrr.metadataservice.dto;

import jakarta.validation.constraints.NotNull;

public record MetadataProcessRequest(
        @NotNull(message = "file id is required") Long fileId,

        String minioObjectKey) {
}
