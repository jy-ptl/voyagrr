package com.voyagrr.storageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EncodingCompletedEvent {

    private String fileId;
    private String minioObjectKey;

}
