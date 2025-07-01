package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.service.DirectoryService;
import com.voyagrr.storageservice.service.grpc.SharingPermissionGrpcClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final SharingPermissionGrpcClient sharingPermissionGrpcClient;

    @Override
    public Directory findDirectoryById(Long directoryId) {
        return directoryRepository.findById(directoryId).orElseThrow(() -> new EntityNotFoundException("Directory with id : " + directoryId + " does not exists."));
    }

    @Override
    public Long create(DirectoryCreateRequest request, String keycloakUserId) {
        Directory directory = new Directory();
        if (request.parentDirectoryId() != null) {
            Directory parentDirectory = new Directory();
            parentDirectory.setId(request.parentDirectoryId());
            directory.setParentDirectory(parentDirectory);
        }
        directory.setName(request.name());
        directory.setOwnerId(keycloakUserId);
        Long directoryId = directoryRepository.save(directory).getId();
        boolean defaultPermissionsGenerated = sharingPermissionGrpcClient.createDefaultPermissions(keycloakUserId, directoryId);
        if (!defaultPermissionsGenerated)
            log.error("Unable to create default permissions for directory with id {}", directoryId);
        return directoryId;
    }

    @Override
    public String buildMinioObjectPathFromDirectoryId(Long directoryId) {
        return  directoryRepository.buildMinioObjectPathFromDirectoryId(directoryId);
    }

}
