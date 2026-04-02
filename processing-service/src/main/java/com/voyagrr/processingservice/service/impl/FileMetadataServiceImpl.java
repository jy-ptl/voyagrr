package com.voyagrr.processingservice.service.impl;

import java.util.Collections;
import java.util.List;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.enumeration.Permission;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.processingservice.dto.FileMetadataRequest;
import com.voyagrr.processingservice.dto.FileMetadataResponse;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.FileMetadataService;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.processingservice.utility.FileMetadataMapper;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;
    private final StorageGrpcClient storageGrpcClient;
    private final FileMetadataMapper fileMetadataMapper;

    @Override
    public List<FileMetadataResponse> getFileMetadata(FileMetadataRequest request, String keycloakUserId) {
        if (request.fileId() != null)
            return handleFileRequest(request.fileId(), keycloakUserId);
        return handleDirectoryRequest(request.directoryId(), keycloakUserId);
    }

    private List<FileMetadataResponse> handleFileRequest(long fileId, String keycloakUserId) {
        if (storageGrpcClient.hasPermissionForFile(keycloakUserId, fileId, Permission.VIEW.name()))
            return fileMetadataMapper
                    .fileMetadataToFileMetadataResponse(fileMetadataRepository.getFileMetadataByFileId(fileId));
        throw new AccessDeniedException(ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.VIEW,
                ExceptionConstant.RESOURCES.FILE));
    }

    private List<FileMetadataResponse> handleDirectoryRequest(long directoryId, String keycloakUserId) {
        List<Long> fileIds = storageGrpcClient.getFileIdsOfDirectory(keycloakUserId, directoryId,
                Permission.VIEW.name());
        if (fileIds.isEmpty())
            return Collections.emptyList();
        return fileMetadataMapper
                .fileMetadataToFileMetadataResponse(fileMetadataRepository.getFileMetadataByFileIds(fileIds));
    }

}
