package com.voyagrr.sharingservice.service;

import com.voyagrr.common.proto.ContentAccessResponse;
import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;

import java.util.List;
import java.util.stream.LongStream;

public interface MediaShareService {
    boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission);

    String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId);

    void createDefaultPermissions(Long directoryId, String keycloakUserId);

    boolean deleteAllPermissionByDirectoryIds(List<Long> directoryIds);

    boolean deleteAllPermissionByUserIds(List<String> userIds);

    boolean deleteAllPermissionByFileIds(List<Long> fileIds);

    boolean deleteAllPermissionByGroupIds(List<Long> groupIds);

    ContentAccessResponse contentAccessOfDirectoryByDirectoryIdAndUserId(Long directoryId, List<Long> directoryIds, List<Long> fileIds, String keycloakUserId);
}
