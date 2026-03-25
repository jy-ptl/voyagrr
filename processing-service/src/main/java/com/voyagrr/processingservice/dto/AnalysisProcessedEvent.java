package com.voyagrr.processingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisProcessedEvent {

    private String fileId;
    private Object result;
    private String minioObjectKey;
    private String mime;

}
