package com.voyagrr.storageservice.dto;

import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
        String name,
        MultipartFile file,
        String path
) {
}
