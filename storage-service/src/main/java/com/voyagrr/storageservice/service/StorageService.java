package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.model.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StorageService {

    String upload(FileUploadRequest request, MultipartFile file, String keycloakUserId);

    void deleteFiles(List<File> files);

    Resource download(long fileId, String keycloakUserId);

    String deleteFile(long fileId, String keycloakUserId);

    String getMinioObjectKey(Long fileId, String keycloakUserId);
}
