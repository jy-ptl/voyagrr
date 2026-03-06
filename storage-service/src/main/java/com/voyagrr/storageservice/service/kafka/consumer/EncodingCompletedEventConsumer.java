package com.voyagrr.storageservice.service.kafka.consumer;

import com.voyagrr.storageservice.dto.EncodingCompletedEvent;
import com.voyagrr.storageservice.dto.FileUploadedEvent;
import com.voyagrr.storageservice.enumeration.EncodingStatus;
import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.kafka.producer.FileAnalyzeEventProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncodingCompletedEventConsumer {

    @Value("${minio.bucket}")
    private String bucket;

    private final FileRepository fileRepository;
    private final FileAnalyzeEventProducer fileAnalyzeEventProducer;

    @Transactional
    @KafkaListener(topics = "file.encoding.processed.v1", groupId = "encoding-processed-response-handles")
    public void consume(EncodingCompletedEvent event) {

        if (event == null || event.getFileId() == null) {
            log.info("Received invalid encoding completed event: {}", event);
            return;
        }

        Long fileId;
        try {
            fileId = Long.parseLong(event.getFileId());
        } catch (NumberFormatException e) {
            log.error("Invalid fileId format: {}", event.getFileId());
            return;
        }

        fileRepository.findById(fileId).ifPresentOrElse(file -> {

            log.info("File {} encoding status updated to COMPLETED", fileId);
            if (file.getMimeType().contains("image")) {
                file.setEncodingStatus(EncodingStatus.IN_ANALYSIS);
                fileRepository.save(file);

                log.info("starting analysis for file {}", fileId);
                fileAnalyzeEventProducer.sendAnalyzeEvent(FileUploadedEvent
                        .builder()
                        .fileId(file.getId().toString())
                        .objectKey(file.getMinioObjectKey())
                        .bucket(bucket)
                        .build());
            } else if (file.getMimeType().contains("video")) {
                file.setEncodingStatus(EncodingStatus.COMPLETED);
                fileRepository.save(file);
                log.info("file {} is encoded", fileId);
            }
        }, () -> log.warn("File {} not found in database", fileId));
    }
}
