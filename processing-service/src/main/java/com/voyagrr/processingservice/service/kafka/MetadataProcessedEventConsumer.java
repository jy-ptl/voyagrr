package com.voyagrr.processingservice.service.kafka;

import com.voyagrr.processingservice.dto.MetadataProcessedEvent;
import com.voyagrr.processingservice.model.FileMetadata;
import com.voyagrr.processingservice.repository.FileMetadataRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataProcessedEventConsumer {

    private final FileMetadataRepository fileMetadataRepository;

    @KafkaListener(topics = "file.metadata.extracted.v1", groupId = "metadata-processing-response-handles")
    public void consume(MetadataProcessedEvent event) {

        log.info("received metadata for file {}", event.getFileId());
        fileMetadataRepository.save(FileMetadata.builder()
                .mimeType(event.getMime())
                .minioObjectKey(event.getMinioObjectKey())
                .fileId(Long.parseLong(event.getFileId()))
                .metadata(event.getMetadata())
                .build());

    }

}
