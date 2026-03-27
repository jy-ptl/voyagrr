package com.voyagrr.processingservice.dto;

import java.util.Map;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataResponse {
    private long fileId;
    private Map<String, Object> metadata;
}
