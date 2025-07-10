package com.voyagrr.storageservice.service.grpc;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Component
@RequiredArgsConstructor
public class SharingPermissionGrpcClient {

    @GrpcClient("sharing-permission-service")
    private SharingPermissionServiceGrpc.SharingPermissionServiceBlockingStub stub;

    public boolean hasPermission(String userId, Long directoryId, String permission) {
        HasPermissionRequest request = HasPermissionRequest.newBuilder()
                .setUserId(userId)
                .setPermission(permission)
                .setDirectoryId(directoryId)
                .build();

        HasPermissionResponse response = stub.hasPermission(request);
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

}
