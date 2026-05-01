package com.voyagrr.processingservice.service.kafka.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.voyagrr.processingservice.dto.TripAnalyzedEvent;
import com.voyagrr.processingservice.model.FileMetadata;
import com.voyagrr.processingservice.repository.FileMetadataRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TripAnalyzedEventConsumer {

    private final FileMetadataRepository fileMetadataRepository;

    @KafkaListener(topics = "trip.analyzed.v1", groupId = "trip-analyzed-response-handler", containerFactory = "tripAnalyzedKafkaListenerContainerFactory")
    public void consume(TripAnalyzedEvent event) {
        log.info("received recognition results for trip {} image {}", event.getTripId(), event.getImagePath());

        FileMetadata fileMetadata = fileMetadataRepository.findByMinioObjectKey(event.getImagePath())
                .orElse(null);

        if (fileMetadata == null) {
            log.warn("no file_metadata record found for imagePath={}, skipping recognition storage",
                    event.getImagePath());
            return;
        }

        if (fileMetadata.getMetadata() == null) {
            fileMetadata.setMetadata(new HashMap<>());
        }

        List<Map<String, Object>> faceResults = event.getFaces().stream()
                .map(face -> {
                    Map<String, Object> faceMap = new HashMap<>();
                    faceMap.put("userId", face.getUserId());
                    faceMap.put("confidence", face.getConfidence());
                    if (face.getBbox() != null) {
                        Map<String, Object> bboxMap = new HashMap<>();
                        bboxMap.put("x1", face.getBbox().getX1());
                        bboxMap.put("y1", face.getBbox().getY1());
                        bboxMap.put("x2", face.getBbox().getX2());
                        bboxMap.put("y2", face.getBbox().getY2());
                        faceMap.put("bbox", bboxMap);
                    }
                    return faceMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> recognitionData = new HashMap<>();
        recognitionData.put("tripId", event.getTripId());
        recognitionData.put("faces", faceResults);

        fileMetadata.getMetadata().put("recognition", recognitionData);
        fileMetadataRepository.save(fileMetadata);

        log.info("stored recognition results for file {} ({}): {} faces",
                fileMetadata.getFileId(), event.getImagePath(), faceResults.size());
    }

}
