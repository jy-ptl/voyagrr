package com.voyagrr.processingservice.service.kafka.consumer;

import com.voyagrr.common.enumeration.FileStatus;
import com.voyagrr.processingservice.dto.EncodingProcessedEvent;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncodingProcessedEventConsumer {

    private final StorageGrpcClient storageGrpcClient;

    @KafkaListener(topics = "file.encoded.v1", groupId = "encoding-processed-response-handler", containerFactory = "encodingKafkaListenerContainerFactory")
    public void consume(EncodingProcessedEvent event) {
        log.info("encoding process completed for file {}", event.getFileId());
        storageGrpcClient.updateFileProcessingStatus(Long.parseLong(event.getFileId()),
                FileStatus.ENCODING_PROCESS_COMPLETED.name());
    }

}
