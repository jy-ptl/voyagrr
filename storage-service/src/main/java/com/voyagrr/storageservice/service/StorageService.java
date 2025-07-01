package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.FileUploadRequest;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(FileUploadRequest request, MultipartFile file, String keycloakUserId);
}
