package com.voyagrr.processingservice.service.kafka.producer;

import com.voyagrr.processingservice.dto.ImageEmbeddingEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageEmbeddingEventProducer {

    private static final String TOPIC = "image.embedding.v1";

    private final KafkaTemplate<String, ImageEmbeddingEvent> kafkaTemplate;

    public void sendImageEmbeddingEvent(ImageEmbeddingEvent event) {
        kafkaTemplate.send(TOPIC, event.getKeycloakUserId(), event);
    }

}
