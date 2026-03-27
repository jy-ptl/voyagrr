package com.voyagrr.processingservice.dto;

public record FileMetadataRequest(Long fileId, Long directoryId) {
    public FileMetadataRequest {
        if ((fileId == null && directoryId == null) ||
                (fileId != null && directoryId != null)) {
            throw new IllegalArgumentException("Provide exactly one of fileId or directoryId");
        }
    }
}
