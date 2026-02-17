package com.voyagrr.processingservice.service;

import com.voyagrr.processingservice.dto.ProcessRequest;

public interface ProcessingService {
    void extractMetadata(ProcessRequest processRequest, String keycloakUserId);
}
