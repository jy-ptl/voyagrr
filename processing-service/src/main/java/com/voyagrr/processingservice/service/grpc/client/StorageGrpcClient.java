package com.voyagrr.processingservice.service.grpc.client;

import java.util.List;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceBlockingStub stub;

    public boolean updateFileProcessingStatus(long fileId, String status) {
        return stub
                .updateFileProcessingStatus(
                        UpdateFileProcessingStatusRequest.newBuilder().setFileId(fileId).setStatus(status).build())
                .getSuccess();

    }

    public boolean hasPermissionForFile(String keycloakUserId, long fileId, String permission) {
        return stub.hasPermissionForFile(
                HasPermissionForFileRequest.newBuilder()
                        .setKeycloakUserId(keycloakUserId)
                        .setFileId(fileId)
                        .setPermission(permission)
                        .build())
                .getAllowed();
    }

    public List<Long> getFileIdsOfDirectory(String keycloakUserId, long directoryId, String permission) {
        return stub.getFileIdsOfDirectory(GetFileIdsOfDirectoryRequest.newBuilder().setDirectoryId(directoryId)
                .setKeycloakUserId(keycloakUserId).setPermission(permission).build()).getFileIdList();
    }

    public GetTripProcessingDataResponse getTripData(Long tripId, Long groupId) {
        return stub.getTripProcessingData(
                GetTripProcessingDataRequest.newBuilder()
                        .setTripId(tripId)
                        .setGroupId(groupId)
                        .build());
    }

}
