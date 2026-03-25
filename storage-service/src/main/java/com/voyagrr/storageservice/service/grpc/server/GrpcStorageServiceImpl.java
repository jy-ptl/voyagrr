package com.voyagrr.storageservice.service.grpc.server;

import com.voyagrr.common.proto.*;
import com.voyagrr.storageservice.service.FileService;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcStorageServiceImpl extends StorageServiceGrpc.StorageServiceImplBase {

    private final FileService fileService;

    @Override
    public void updateFileProcessingStatus(UpdateFileProcessingStatusRequest request,
            StreamObserver<UpdateFileProcessingStatusResponse> responseObserver) {
        responseObserver.onNext(UpdateFileProcessingStatusResponse.newBuilder()
                .setSuccess(fileService.updateFileStatus(request.getFileId(), request.getStatus())).build());
        responseObserver.onCompleted();
    }
}
