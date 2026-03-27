package com.voyagrr.processingservice.utility;

import java.util.List;

import com.voyagrr.processingservice.dto.FileMetadataResponse;
import com.voyagrr.processingservice.model.FileMetadata;

import org.springframework.stereotype.Component;

@Component
public class FileMetadataMapper {

    public List<FileMetadataResponse> fileMetadataToFileMetadataResponse(List<FileMetadata> metadatas) {
        return metadatas.stream().map(fileMetadata -> FileMetadataResponse
                .builder()
                .fileId(fileMetadata.getFileId())
                .metadata(fileMetadata.getMetadata())
                .build()).toList();
    }

}
