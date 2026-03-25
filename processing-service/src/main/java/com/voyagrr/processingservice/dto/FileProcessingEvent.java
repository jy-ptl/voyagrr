package com.voyagrr.processingservice.dto;

import java.time.Instant;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileProcessingEvent {

    private String eventId;
    private Instant timestamp;

    private String fileId;
    private String minioObjectKey;

}
