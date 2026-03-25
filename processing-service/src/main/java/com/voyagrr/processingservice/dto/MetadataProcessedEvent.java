package com.voyagrr.processingservice.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetadataProcessedEvent {

    private String fileId;
    private String minioObjectKey;
    private Map<String, Object> metadata;

}
