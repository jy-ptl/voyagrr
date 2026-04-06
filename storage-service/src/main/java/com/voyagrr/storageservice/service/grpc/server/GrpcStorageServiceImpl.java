package com.voyagrr.storageservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.storageservice.service.DirectoryService;
import com.voyagrr.storageservice.service.FileService;
import com.voyagrr.storageservice.service.GroupService;
import com.voyagrr.storageservice.service.MediaShareService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcStorageServiceImpl extends StorageServiceGrpc.StorageServiceImplBase {

    private final FileService fileService;
    private final MediaShareService mediaShareService;
    private final DirectoryService directoryService;
    private final GroupService groupService;

    @Override
    public void updateFileProcessingStatus(UpdateFileProcessingStatusRequest request,
            StreamObserver<UpdateFileProcessingStatusResponse> responseObserver) {
        responseObserver.onNext(UpdateFileProcessingStatusResponse.newBuilder()
                .setSuccess(fileService.updateFileStatus(request.getFileId(), request.getStatus())).build());
        responseObserver.onCompleted();
    }

    @Override
    public void hasPermissionForFile(HasPermissionForFileRequest request,
            StreamObserver<HasPermissionForFileResponse> responseObserver) {
        responseObserver.onNext(
                HasPermissionForFileResponse.newBuilder()
                        .setAllowed(
                                mediaShareService.hasPermissionForFile(request.getKeycloakUserId(), request.getFileId(),
                                        request.getPermission()))
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFileIdsOfDirectory(GetFileIdsOfDirectoryRequest request,
            StreamObserver<GetFileIdsOfDirectoryResponse> responseObserver) {

        responseObserver.onNext(GetFileIdsOfDirectoryResponse.newBuilder()
                .addAllFileId(mediaShareService.getFileIdsOfDirectory(request.getKeycloakUserId(),
                        request.getDirectoryId(), request.getPermission()))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void createDirectoryForTrip(CreateDirectoryForTripRequest request,
            StreamObserver<CreateDirectoryForTripResponse> responseObserver) {

        responseObserver.onNext(
                CreateDirectoryForTripResponse.newBuilder().setDirectoryId(
                        directoryService.createDiretoryForTrip(request.getDirectoryName(), request.getKeycloakUserId()))
                        .build());
        responseObserver.onCompleted();
    }

    public void createOrValidateGroupForTrip(CreateOrValidateGroupForTripRequest request,
            StreamObserver<CreateOrValidateGroupForTripResponse> responseObserver) {

        responseObserver.onNext(CreateOrValidateGroupForTripResponse.newBuilder()
                .setGroupId(groupService.createOrValidateGroupForTrip(request.getGroupId(), request.getGroupName(),
                        request.getOwnerId(), request.getKeycloakUserIdList()))
                .build());
        responseObserver.onCompleted();
    };

}
