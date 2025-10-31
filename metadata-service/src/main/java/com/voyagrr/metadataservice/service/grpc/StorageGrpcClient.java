package com.voyagrr.metadataservice.service.grpc;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceBlockingStub stub;

    public String getMinioObjectKeyFromFileId(long fileId, String keycloakUserId) {
        return stub.getMinioObjectKeyFromFileId(MinioObjectKeyRequest.newBuilder()
                .setFileId(fileId)
                .setKeycloakUserId(keycloakUserId)
                .build()).getMinioObjectKey();
    }
}
