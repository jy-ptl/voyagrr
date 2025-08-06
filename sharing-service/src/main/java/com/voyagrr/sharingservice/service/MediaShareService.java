package com.voyagrr.sharingservice.service;

import com.voyagrr.common.proto.ContentAccessResponse;
import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;
import com.voyagrr.sharingservice.dto.FilePermissionRequest;

import java.util.List;

public interface MediaShareService {
    boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission);

    String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId);

    void createDefaultPermissions(Long directoryId, String keycloakUserId);

    boolean deleteAllPermissionByDirectoryIds(List<Long> directoryIds);

    boolean deleteAllPermissionByUserIds(List<String> userIds);

    boolean deleteAllPermissionByFileIds(List<Long> fileIds);

    boolean deleteAllPermissionByGroupIds(List<Long> groupIds);

    ContentAccessResponse contentAccessOfDirectoryByDirectoryIdAndUserId(List<Long> ancestorsIncludingSelf,
            List<Long> directoryIds, List<Long> fileIds, String keycloakUserId);

    boolean hasPermissionForDirectories(String keycloakUserId, List<Long> directoryIds, String permission);

    boolean hasPermissionForFile(String userId, long fileId, String permission);

    String updateFilePermission(FilePermissionRequest request, String keycloakUserId);
}
