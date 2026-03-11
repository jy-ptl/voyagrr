package com.voyagrr.sharingservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.sharingservice.service.MediaShareService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class GrpcSharingServiceImpl extends SharingServiceGrpc.SharingServiceImplBase {

    private final MediaShareService mediaShareService;

    @Override
    public void hasPermissionForDirectory(HasPermissionDirectoryRequest request,
            StreamObserver<HasPermissionDirectoryResponse> responseObserver) {

        Long directoryId = request.getDirectoryId();
        String keycloakUserId = request.getUserId();
        String permission = request.getPermission();

        HasPermissionDirectoryResponse response = HasPermissionDirectoryResponse.newBuilder()
                .setAllowed(mediaShareService.hasPermissionForDirectory(directoryId, keycloakUserId, permission))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void hasPermissionForDirectories(HasPermissionForDirectoriesRequest request,
            StreamObserver<HasPermissionForDirectoriesResponse> responseObserver) {
        String keycloakUserId = request.getUserId();
        String permission = request.getPermission();

        HasPermissionForDirectoriesResponse response = HasPermissionForDirectoriesResponse.newBuilder()
                .setAllowed(mediaShareService.hasPermissionForDirectories(keycloakUserId, request.getDirectoryIdList(),
                        permission))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void hasPermissionForFile(HasPermissionForFileRequest request,
            StreamObserver<HasPermissionForFileResponse> responseObserver) {
        responseObserver.onNext(
                HasPermissionForFileResponse.newBuilder()
                        .setAllowed(mediaShareService.hasPermissionForFile(request.getUserId(), request.getFileId(),
                                request.getPermission()))
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void createDefaultPermission(CreateDefaultPermissionRequest request,
            StreamObserver<CreateDefaultPermissionResponse> responseObserver) {

        Long directoryId = request.getDirectoryId();
        String keycloakUserId = request.getUserId();

        mediaShareService.createDefaultPermissions(directoryId, keycloakUserId);

        CreateDefaultPermissionResponse response = CreateDefaultPermissionResponse.newBuilder()
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deletePermission(DeletePermissionRequest request,
            StreamObserver<DeletePermissionResponse> responseObserver) {

        boolean success;
        switch (request.getType()) {
            case USER ->
                success = mediaShareService.deleteAllPermissionByUserIds(
                        request.getDeletePermissionList().stream().map(DeletePermissionDto::getUserId).toList());
            case FILE ->
                success = mediaShareService.deleteAllPermissionByFileIds(request.getDeletePermissionList().stream()
                        .mapToLong(DeletePermissionDto::getFileId).boxed().toList());
            case GROUP ->
                success = mediaShareService.deleteAllPermissionByGroupIds(request.getDeletePermissionList().stream()
                        .mapToLong(DeletePermissionDto::getGroupId).boxed().toList());
            case DIRECTORY ->
                success = mediaShareService.deleteAllPermissionByDirectoryIds(request.getDeletePermissionList().stream()
                        .mapToLong(DeletePermissionDto::getDirectoryId).boxed().toList());
            default -> throw new IllegalStateException("Unexpected value: " + request.getType());
        }

        DeletePermissionResponse response = DeletePermissionResponse.newBuilder()
                .setSuccess(success)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void contentAccessOfDirectory(ContentAccessRequest request,
            StreamObserver<ContentAccessResponse> responseObserver) {

        List<Long> ancestorsIncludingSelf = request.getDirectoryIdList();
        List<Long> directoryIds = request.getChildDirectoryIdList();
        List<Long> fileIds = request.getFileIdList();
        String keycloakUserId = request.getUserId();

        ContentAccessResponse response = mediaShareService.contentAccessOfDirectoryByDirectoryIdAndUserId(
                ancestorsIncludingSelf, directoryIds, fileIds, keycloakUserId);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
