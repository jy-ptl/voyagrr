package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.dto.DirectoryContentResponse;
import com.voyagrr.storageservice.dto.DirectoryResponse;
import com.voyagrr.storageservice.dto.DirectoryTreeResponse;
import com.voyagrr.storageservice.dto.FileThumbnailResponse;
import com.voyagrr.storageservice.model.Directory;

import java.util.List;

public interface DirectoryService {

    Directory findDirectoryById(Long directoryId);

    Long create(DirectoryCreateRequest request, String keycloakUserId);

    String buildMinioObjectPathFromDirectoryId(Long directoryId);

    List<DirectoryTreeResponse> getAllDirectoriesOfUser(String keycloakUserId);

    String deleteDirectoryById(Long directoryId, String keycloakUserId);

    DirectoryContentResponse getDirectoryContents(Long directoryId, String keycloakUserId);

    List<Long> getAllAncestorsIncludingSelfFromFileId(long fileId);

    Long createDirectoryForTrip(String directoryName, String keycloakUserId);

    Long createDefaultSampleDirectoryForUser(String keycloakUserId);

    Long getSampleDirectoryIdByUserId(String keycloakUserId);

    List<FileThumbnailResponse> getThumbnailsForDirectory(long directoryId, String keycloakUserId);
    DirectoryResponse getSampleDirectory(String keycloakUserId);
}
