package com.voyagrr.storageservice.service.kafka.consumer;

import com.voyagrr.storageservice.dto.EncodingCompletedEvent;
import com.voyagrr.storageservice.enumeration.EncodingStatus;
import com.voyagrr.storageservice.repository.FileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncodingCompletedEventConsumer {

    private final FileRepository fileRepository;

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

            if (file.getEncodingStatus() == EncodingStatus.COMPLETED) {
                log.info("File {} already marked as COMPLETED, skipping update", fileId);
                return;
            }

            log.info("File {} encoding status updated to COMPLETED", fileId);
            file.setEncodingStatus(EncodingStatus.COMPLETED);
            fileRepository.save(file);
        }, () -> log.warn("File {} not found in database", fileId));
    }
}
