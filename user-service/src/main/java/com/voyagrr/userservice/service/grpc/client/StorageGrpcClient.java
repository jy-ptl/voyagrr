package com.voyagrr.userservice.service.grpc.client;

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

    public long createDefaultSampleDirectoryForUser(String keycloakUserId) {
        return stub
                .createDefaultSampleDirectoryForUser(
                        CreateDefaultSampleDirectoryForUserRequest.newBuilder().setKeycloakUserId(keycloakUserId)
                                .build())
                .getDirectoryId();
    }

}
