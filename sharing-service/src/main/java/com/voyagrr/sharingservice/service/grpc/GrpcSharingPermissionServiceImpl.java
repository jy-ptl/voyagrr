package com.voyagrr.sharingservice.service.grpc;

import com.voyagrr.common.proto.*;
import com.voyagrr.sharingservice.service.MediaShareService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcSharingPermissionServiceImpl extends SharingPermissionServiceGrpc.SharingPermissionServiceImplBase {

    private final MediaShareService mediaShareService;

    @Override
    public void hasPermission(HasPermissionRequest request, StreamObserver<HasPermissionResponse> responseObserver) {

        Long directoryId = request.getDirectoryId();
        String keycloakUserId = request.getUserId();
        String permission = request.getPermission();

        HasPermissionResponse response = HasPermissionResponse.newBuilder()
                .setAllowed(mediaShareService.hasPermissionForDirectory(directoryId, keycloakUserId, permission))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createDefaultPermission(CreateDefaultPermissionRequest request, StreamObserver<CreateDefaultPermissionResponse> responseObserver) {

        Long directoryId = request.getDirectoryId();
        String keycloakUserId = request.getUserId();

        mediaShareService.createDefaultPermissions(directoryId, keycloakUserId);

        CreateDefaultPermissionResponse response = CreateDefaultPermissionResponse.newBuilder()
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
