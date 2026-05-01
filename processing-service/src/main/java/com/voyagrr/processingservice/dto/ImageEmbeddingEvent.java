package com.voyagrr.processingservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageEmbeddingEvent {
    private String keycloakUserId;
    private String sampleDirectory;
}
