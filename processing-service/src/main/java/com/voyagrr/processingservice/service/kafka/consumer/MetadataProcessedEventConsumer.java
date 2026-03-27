package com.voyagrr.processingservice.service.kafka.consumer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.processingservice.dto.FileProcessingEvent;
import com.voyagrr.processingservice.dto.MetadataProcessedEvent;
import com.voyagrr.processingservice.model.FileMetadata;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.processingservice.service.kafka.producer.ImageAnalysisEventProducer;
import com.voyagrr.processingservice.service.kafka.producer.VideoEncodingEventProducer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataProcessedEventConsumer {

    private final FileMetadataRepository fileMetadataRepository;
    private final VideoEncodingEventProducer videoEncodingEventProducer;
    private final ImageAnalysisEventProducer imageAnalysisEventProducer;
    private final StorageGrpcClient storageGrpcClient;

    @KafkaListener(topics = "file.metadata.v1", groupId = "metadata-processing-response-handles", containerFactory = "metadataKafkaListenerContainerFactory")
    public void consume(MetadataProcessedEvent event) {

        log.info("received metadata for file {}", event.getFileId());
        Long fileId = Long.parseLong(event.getFileId());

        FileMetadata fileMetadata = fileMetadataRepository
                .findByFileId(fileId)
                .orElseGet(() -> FileMetadata.builder()
                        .fileId(fileId)
                        .minioObjectKey(event.getMinioObjectKey())
                        .metadata(new HashMap<>())
                        .build());

        @SuppressWarnings("unchecked")
        Map<String, Object> metadataMap = new ObjectMapper().convertValue(event.getMetadata(), Map.class);
        fileMetadata.getMetadata().put("file", metadataMap);

        String mime = metadataMap.getOrDefault("mime", "").toString();

        fileMetadataRepository.save(fileMetadata);
        if (mime.contains("video")) {
            log.info("calling encoding for file {}", event.getFileId());
            videoEncodingEventProducer.sendEncodingEvent(
                    FileProcessingEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .timestamp(Instant.now())
                            .fileId(event.getFileId())
                            .minioObjectKey(event.getMinioObjectKey())
                            .build());
            storageGrpcClient.updateFileProcessingStatus(Long.parseLong(event.getFileId()),
                    FileStatus.IN_ENCODING_PROCESS.name());
        } else if (mime.contains("image")) {
            log.info("calling analysis for file {}", event.getFileId());
            imageAnalysisEventProducer.sendImageAnalysisEvent(
                    FileProcessingEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .timestamp(Instant.now())
                            .fileId(event.getFileId())
                            .minioObjectKey(event.getMinioObjectKey())
                            .build());
            storageGrpcClient.updateFileProcessingStatus(Long.parseLong(event.getFileId()),
                    FileStatus.IN_ANALYSIS_PROCESS.name());
        }

    }

}
