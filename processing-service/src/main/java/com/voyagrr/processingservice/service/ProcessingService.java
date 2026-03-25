package com.voyagrr.processingservice.service;

import com.voyagrr.common.proto.ProcessFileRequest;
import com.voyagrr.processingservice.dto.ProcessRequest;

public interface ProcessingService {
    boolean processFile(ProcessFileRequest request);
}
