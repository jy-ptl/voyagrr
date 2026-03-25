package com.voyagrr.processingservice.service.grpc.client;

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

}
