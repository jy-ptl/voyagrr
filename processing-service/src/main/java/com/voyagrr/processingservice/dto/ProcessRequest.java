package com.voyagrr.processingservice.dto;

import jakarta.validation.constraints.NotNull;

public record ProcessRequest(
        @NotNull(message = "file id is required") Long fileId,

        String minioObjectKey) {
}
