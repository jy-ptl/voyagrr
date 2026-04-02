package com.voyagrr.processingservice.service.impl;

import java.time.Instant;
import java.util.UUID;

import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.common.proto.ProcessFileRequest;
import com.voyagrr.processingservice.dto.FileProcessingEvent;
import com.voyagrr.processingservice.service.ProcessingService;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.processingservice.service.kafka.producer.FileMetadataEventProducer;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    private final FileMetadataEventProducer fileMetadataEventProducer;
    private final StorageGrpcClient storageGrpcClient;

    @Override
    public boolean processFile(ProcessFileRequest request) {
        fileMetadataEventProducer.sendFileUploadedEvent(FileProcessingEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .fileId(String.valueOf(request.getFileId()))
                .minioObjectKey(request.getMinioObjectKey())
                .timestamp(Instant.now())
                .build());
        storageGrpcClient.updateFileProcessingStatus(request.getFileId(), FileStatus.IN_METADATA_PROCESS.name());
        return true;
    }

}
