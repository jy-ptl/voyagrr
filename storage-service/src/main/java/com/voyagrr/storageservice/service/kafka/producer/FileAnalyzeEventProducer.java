package com.voyagrr.storageservice.service.kafka.producer;

import com.voyagrr.storageservice.dto.FileUploadedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileAnalyzeEventProducer {

    private static final String TOPIC = "file.analyze.v1";

    private final KafkaTemplate<String, FileUploadedEvent> kafkaTemplate;

    public void sendAnalyzeEvent(FileUploadedEvent event) {
        kafkaTemplate.send(TOPIC, event.getFileId(), event);
    }

}
