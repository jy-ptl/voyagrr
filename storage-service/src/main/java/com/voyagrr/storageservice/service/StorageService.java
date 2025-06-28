package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.FileUploadRequest;

public interface StorageService {
    String upload(FileUploadRequest request, String keycloakUserId);
}
