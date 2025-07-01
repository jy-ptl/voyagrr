package com.voyagrr.sharingservice.service;

import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;

public interface MediaShareService {
    boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission);

    String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId);

    void createDefaultPermissions(Long directoryId, String keycloakUserId);
}
