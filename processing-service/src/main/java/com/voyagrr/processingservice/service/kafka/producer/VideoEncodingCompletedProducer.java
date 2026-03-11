package com.voyagrr.processingservice.service.kafka.producer;

import com.voyagrr.processingservice.dto.FileUploadedEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoEncodingCompletedProducer {

    private static final String TOPIC = "file.encoding.processed.v1";

    private final KafkaTemplate<String, FileUploadedEvent> kafkaTemplate;

    public void sendEncodingCompletedEvent(FileUploadedEvent event) {
        kafkaTemplate.send(TOPIC, event.getFileId(), event);
    }

}
