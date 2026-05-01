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

    /**
     * Checks if a user has the specified permission on a given file, either via any
     * ancestor directory (including its own directory)
     * or directly on the file itself.
     * <p>
     * This method first checks if the user has the given permission on any ancestor
     * directory of the file
     * (including the directory the file resides in). If such permission exists, it
     * returns {@code true} immediately.
     * Otherwise, it checks if the user has the permission directly on the file.
     * </p>
     *
     * @param file           The {@link File} entity to check.
     * @param keycloakUserId The Keycloak user ID for whom the permission is being
     *                       checked.
     * @param permission     The permission to verify (e.g., "UPLOAD", "DOWNLOAD",
     *                       "DELETE").
     * @return {@code true} if the user has the specified permission on any ancestor
     *         directory or directly on the file;
     *         {@code false} otherwise.
     */
    boolean hasPermissionForFile(String keycloakUserId, long fileId, String permission);

    String updateFilePermission(FilePermissionRequest request, String keycloakUserId);

    List<Long> getFileIdsOfDirectory(String keycloakUserId, long direcotryId, String permission);

    void createDefaultPermissionsForSampleDirectory(Long directoryId, String keycloakUserId);
}
