package com.voyagrr.processingservice.service.kafka.consumer;

import java.util.HashMap;

import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.processingservice.dto.AnalysisProcessedEvent;
import com.voyagrr.processingservice.model.FileMetadata;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisProcessedEventConsumer {

    private final StorageGrpcClient storageGrpcClient;
    private final FileMetadataRepository fileMetadataRepository;

    @KafkaListener(topics = "file.analyzed.v1", groupId = "analysis-processed-response-handler", containerFactory = "analysisKafkaListenerContainerFactory")
    public void consume(AnalysisProcessedEvent event) {
        log.info("analysis process completed for file {}", event.getFileId());
        Long fileId = Long.parseLong(event.getFileId());

        FileMetadata metadata = fileMetadataRepository.findByFileId(fileId)
                .orElseGet(() -> FileMetadata.builder()
                        .fileId(fileId)
                        .minioObjectKey(event.getMinioObjectKey())
                        .metadata(new HashMap<>())
                        .build());

        if (metadata.getMetadata() == null) {
            metadata.setMetadata(new HashMap<>());
        }

        if (!metadata.getMetadata().containsKey("analysis")) {
            metadata.getMetadata().put("analysis", event.getResult());
            fileMetadataRepository.save(metadata);
        }

        storageGrpcClient.updateFileProcessingStatus(Long.parseLong(event.getFileId()),
                FileStatus.ANALYSIS_PROCESS_COMPLETED.toString());
    }

}
