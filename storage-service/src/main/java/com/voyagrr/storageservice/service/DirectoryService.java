package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.dto.DirectoryTreeResponse;
import com.voyagrr.storageservice.model.Directory;

import java.util.List;

public interface DirectoryService {

    Directory findDirectoryById(Long directoryId);

    Long create(DirectoryCreateRequest request, String keycloakUserId);

    String buildMinioObjectPathFromDirectoryId(Long directoryId);

    List<DirectoryTreeResponse> getAllDirectoriesOfUser(String keycloakUserId);

    String deleteDirectoryById(Long directoryId, String keycloakUserId);
}
