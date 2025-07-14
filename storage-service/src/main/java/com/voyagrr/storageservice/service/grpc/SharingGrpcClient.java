package com.voyagrr.storageservice.service.grpc;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SharingGrpcClient {

    @GrpcClient("sharing-service")
    private SharingServiceGrpc.SharingServiceBlockingStub stub;

    public boolean hasPermissionForDirectory(String userId, Long directoryId, String permission) {
        HasPermissionDirectoryRequest request = HasPermissionDirectoryRequest.newBuilder()
                .setUserId(userId)
                .setPermission(permission)
                .setDirectoryId(directoryId)
                .build();

        HasPermissionDirectoryResponse response = stub.hasPermissionForDirectory(request);
        return response.getAllowed();
    }

    public boolean createDefaultPermissions(String userId, Long directoryId) {

        CreateDefaultPermissionRequest request = CreateDefaultPermissionRequest.newBuilder()
                .setUserId(userId)
                .setDirectoryId(directoryId)
                .build();

        CreateDefaultPermissionResponse response = stub.createDefaultPermission(request);
        return response.getSuccess();

    }

    public boolean deletePermission(DeletePermissionRequest deletePermissionRequest) {
        DeletePermissionResponse response = stub.deletePermission(deletePermissionRequest);
        return response.getSuccess();
    }

    public ContentAccessResponse contentAccessOfDirectory(ContentAccessRequest request) {
        return stub.contentAccessOfDirectory(request);
    }

    public boolean hasPermissionForDirectories(String keycloakUserId, List<Long> directoryIds, String permission) {
        return stub.hasPermissionForDirectories(
                HasPermissionForDirectoriesRequest.newBuilder()
                        .setUserId(keycloakUserId)
                        .setPermission(permission)
                        .addAllDirectoryId(directoryIds)
                        .build()
        ).getAllowed();
    }

    public boolean hasPermissionForFile(String keycloakUserId, long fileId, String permission) {
        return stub.hasPermissionForFile(
                HasPermissionForFileRequest.newBuilder()
                        .setUserId(keycloakUserId)
                        .setFileId(fileId)
                        .setPermission(permission)
                        .build()
        ).getAllowed();
    }

}
