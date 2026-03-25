package com.voyagrr.storageservice.service;

import com.voyagrr.storageservice.dto.ContentAccess;
import com.voyagrr.storageservice.dto.DirectoryPermissionRequest;
import com.voyagrr.storageservice.dto.FilePermissionRequest;

import java.util.List;

public interface MediaShareService {
    boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission);

    String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId);

    void createDefaultPermissions(Long directoryId, String keycloakUserId);

    boolean deleteAllPermissionByDirectoryIds(List<Long> directoryIds);

    boolean deleteAllPermissionByUserIds(List<String> userIds);

    boolean deleteAllPermissionByFileIds(List<Long> fileIds);

    boolean deleteAllPermissionByGroupIds(List<Long> groupIds);

    ContentAccess contentAccessOfDirectoryByDirectoryIdAndUserId(List<Long> ancestorsIncludingSelf,
            List<Long> directoryIds, List<Long> fileIds, String keycloakUserId);

    boolean hasPermissionForDirectories(String keycloakUserId, List<Long> directoryIds, String permission);

    boolean hasPermissionForFile(String userId, long fileId, String permission);

    String updateFilePermission(FilePermissionRequest request, String keycloakUserId);
}
