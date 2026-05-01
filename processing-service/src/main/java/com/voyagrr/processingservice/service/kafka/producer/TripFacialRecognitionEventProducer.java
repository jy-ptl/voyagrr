package com.voyagrr.processingservice.service.kafka.producer;

import com.voyagrr.processingservice.dto.TripAnalyzeEvent;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripFacialRecognitionEventProducer {
    private static final String TOPIC = "trip.analyze.v1";

    private final KafkaTemplate<String, TripAnalyzeEvent> kafkaTemplate;

    public void sendTripAnalysisEvent(TripAnalyzeEvent event) {
        kafkaTemplate.send(TOPIC, event.getTripId().toString(), event);
    }

}
