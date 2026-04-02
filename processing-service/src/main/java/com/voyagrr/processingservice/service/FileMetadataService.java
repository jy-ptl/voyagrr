package com.voyagrr.processingservice.service;

import java.util.List;

import com.voyagrr.processingservice.dto.FileMetadataRequest;
import com.voyagrr.processingservice.dto.FileMetadataResponse;

public interface FileMetadataService {
    List<FileMetadataResponse> getFileMetadata(FileMetadataRequest request, String keycloakUserId);
}
