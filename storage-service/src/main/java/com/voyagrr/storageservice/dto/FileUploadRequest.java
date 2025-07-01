package com.voyagrr.storageservice.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        String name,
        Long directoryId
) {
}
