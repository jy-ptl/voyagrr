package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.model.Directory;

public interface DirectoryService {

    Directory findDirectoryById(Long directoryId);

    Long create(DirectoryCreateRequest request, String keycloakUserId);

    String buildMinioObjectPathFromDirectoryId(Long directoryId);

}
