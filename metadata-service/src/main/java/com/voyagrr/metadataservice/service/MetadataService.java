package com.voyagrr.metadataservice.service;

import com.voyagrr.metadataservice.dto.MetadataProcessRequest;

public interface MetadataService {
    void extractMetadata(MetadataProcessRequest metadataProcessRequest, String keycloakUserId);
}
