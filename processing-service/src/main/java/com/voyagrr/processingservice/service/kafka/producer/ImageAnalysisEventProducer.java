package com.voyagrr.processingservice.service.kafka.producer;

import com.voyagrr.processingservice.dto.FileProcessingEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageAnalysisEventProducer {

    private static final String TOPIC = "file.analyze.v1";

    private final KafkaTemplate<String, FileProcessingEvent> kafkaTemplate;

    public void sendImageAnalysisEvent(FileProcessingEvent event) {
        kafkaTemplate.send(TOPIC, event.getFileId(), event);
    }

}
