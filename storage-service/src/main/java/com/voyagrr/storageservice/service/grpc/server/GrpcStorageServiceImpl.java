package com.voyagrr.storageservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.storageservice.dto.DirectoryFlatResponse;
import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.service.DirectoryService;
import com.voyagrr.storageservice.service.StorageService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcStorageServiceImpl extends StorageServiceGrpc.StorageServiceImplBase {

    private final DirectoryService directoryService;
    private final DirectoryRepository directoryRepository;
    private final StorageService storageService;

    @Override
    public void getAllAncestorsIncludingSelf(AncestorsIncludingSelfRequest request,
            StreamObserver<AncestorsIncludingSelfResponse> responseObserver) {
        responseObserver.onNext(AncestorsIncludingSelfResponse.newBuilder()
                .addAllDirectoryId(directoryRepository.getAllAncestorsIncludingSelf(request.getDirectoryId()).stream()
                        .mapToLong(DirectoryFlatResponse::id).boxed().toList())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAllAncestorsIncludingSelfFromFileId(AncestorsIncludingSelfRequestForFile request,
            StreamObserver<AncestorsIncludingSelfResponse> responseObserver) {
        responseObserver.onNext(
                AncestorsIncludingSelfResponse.newBuilder()
                        .addAllDirectoryId(directoryService.getAllAncestorsIncludingSelfFromFileId(request.getFileId()))
                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMinioObjectKeyFromFileId(MinioObjectKeyRequest request,
            StreamObserver<MinioObjectKeyResponse> responseObserver) {
        responseObserver.onNext(
                MinioObjectKeyResponse.newBuilder()
                        .setMinioObjectKey(
                                storageService.getMinioObjectKey(request.getFileId(), request.getKeycloakUserId()))
                        .build());
        responseObserver.onCompleted();

    }
}
