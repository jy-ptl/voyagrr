package com.voyagrr.processingservice.dto;

import java.time.Instant;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadedEvent {

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private String fileId;
    private String ownerId;

    private String bucket;
    private String objectKey;

    private String status;

}
