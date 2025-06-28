package com.voyagrr.storageservice.service.impl;

import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {


    @Override
    public String upload(FileUploadRequest request, String keycloakUserId) {

        return "";
    }
}
