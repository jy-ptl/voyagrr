package com.voyagrr.processingservice.service.impl;

import java.time.Instant;
import java.util.UUID;

import com.voyagrr.processingservice.dto.FileUploadedEvent;
import com.voyagrr.processingservice.dto.ProcessRequest;
import com.voyagrr.processingservice.service.ProcessingService;
import com.voyagrr.processingservice.service.grpc.StorageGrpcClient;
import com.voyagrr.processingservice.service.kafka.FileEventProducer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    @Value("${minio.bucket}")
    private String bucket;

    private final StorageGrpcClient storageGrpcClient;

    private final FileEventProducer fileEventProducer;

    @Async
    @Override
    public void extractMetadata(ProcessRequest processRequest, String keycloakUserId) {

        String minioObjectKey = storageGrpcClient.getMinioObjectKeyFromFileId(processRequest.fileId(),
                keycloakUserId);

        if (minioObjectKey.equals(""))
            return;

        fileEventProducer.sendUploadedEvent(
                FileUploadedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("FILE_UPLOADED")
                        .bucket(bucket)
                        .timestamp(Instant.now())
                        .fileId(String.valueOf(processRequest.fileId()))
                        .ownerId(keycloakUserId)
                        .objectKey(minioObjectKey)
                        .status("UPLOADED")
                        .build());

    }

}
