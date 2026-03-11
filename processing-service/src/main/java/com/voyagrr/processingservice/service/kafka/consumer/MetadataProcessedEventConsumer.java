package com.voyagrr.processingservice.service.kafka.consumer;

import java.time.Instant;
import java.util.UUID;

import com.voyagrr.processingservice.dto.FileUploadedEvent;
import com.voyagrr.processingservice.dto.MetadataProcessedEvent;
import com.voyagrr.processingservice.model.FileMetadata;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.kafka.producer.VideoEncodingCompletedProducer;
import com.voyagrr.processingservice.service.kafka.producer.VideoEncodingEventProducer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataProcessedEventConsumer {

    @Value("${minio.bucket}")
    private String bucket;

    private final FileMetadataRepository fileMetadataRepository;
    private final VideoEncodingEventProducer videoEncodingEventProducer;
    private final VideoEncodingCompletedProducer videoEncodingCompletedProducer;

    @KafkaListener(topics = "file.metadata.extracted.v1", groupId = "metadata-processing-response-handles")
    public void consume(MetadataProcessedEvent event) {

        log.info("received metadata for file {}", event.getFileId());

        if (fileMetadataRepository.findByFileId(Long.parseLong(event.getFileId())).isPresent())
            return;

        if (event.getMime().contains("video")) {
            log.info("calling encoding for file {}", event.getFileId());
            videoEncodingEventProducer.sendEncodingEvent(
                    FileUploadedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("FILE_ENCODE")
                            .bucket(bucket)
                            .timestamp(Instant.now())
                            .fileId(event.getFileId())
                            .objectKey(event.getMinioObjectKey())
                            .status("UPLOADED")
                            .build());
        } else if (event.getMime().contains("image")) {
            videoEncodingCompletedProducer.sendEncodingCompletedEvent(
                    FileUploadedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("FILE_ENCODE_COMPLETE")
                            .bucket(bucket)
                            .timestamp(Instant.now())
                            .fileId(event.getFileId())
                            .objectKey(event.getMinioObjectKey())
                            .status("ENCODED")
                            .build());
        }

        fileMetadataRepository.save(FileMetadata.builder()
                .mimeType(event.getMime())
                .minioObjectKey(event.getMinioObjectKey())
                .fileId(Long.parseLong(event.getFileId()))
                .metadata(event.getMetadata())
                .build());

    }

}
