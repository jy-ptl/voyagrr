package com.voyagrr.storageservice.service.grpc.client;

import com.voyagrr.common.proto.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessingGrpcClient {

    @GrpcClient("processing-service")
    private ProcessingServiceGrpc.ProcessingServiceBlockingStub stub;

    public boolean startFileProcessing(long fileId, String minioObjectKey) {
        return stub.processFile(ProcessFileRequest.newBuilder().setFileId(fileId).setMinioObjectKey(minioObjectKey)
                .build()).getSuccess();
    }

    public boolean startSampleImageEmbedding(String keycloakUserId, String sampleDirectory) {
        return stub.embeddSampleImages(EmbeddSampleImagesRequest.newBuilder().setKeycloakUserId(keycloakUserId)
                .setSampleDirectory(sampleDirectory).build()).getSuccess();
    }

}
