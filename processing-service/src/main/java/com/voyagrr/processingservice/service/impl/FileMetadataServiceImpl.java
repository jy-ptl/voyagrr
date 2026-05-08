package com.voyagrr.processingservice.service.impl;

import java.util.Collections;
import java.util.List;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.enumeration.Permission;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.proto.UserInfo;
import com.voyagrr.processingservice.dto.FileMetadataRequest;
import com.voyagrr.processingservice.dto.FileMetadataResponse;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.FileMetadataService;
import com.voyagrr.processingservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.processingservice.service.grpc.client.UserGrpcClient;
import com.voyagrr.processingservice.utility.FileMetadataMapper;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataServiceImpl implements FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;
    private final StorageGrpcClient storageGrpcClient;
    private final UserGrpcClient userGrpcClient;
    private final FileMetadataMapper fileMetadataMapper;

    @Override
    public List<FileMetadataResponse> getFileMetadata(FileMetadataRequest request, String keycloakUserId) {
        if (request.fileId() != null)
            return handleFileRequest(request.fileId(), keycloakUserId);
        return handleDirectoryRequest(request.directoryId(), keycloakUserId);
    }

    private List<FileMetadataResponse> handleFileRequest(long fileId, String keycloakUserId) {
        if (storageGrpcClient.hasPermissionForFile(keycloakUserId, fileId, Permission.VIEW.name())) {
            List<FileMetadataResponse> responses = fileMetadataMapper
                    .fileMetadataToFileMetadataResponse(fileMetadataRepository.getFileMetadataByFileId(fileId));
            enrichWithUserInfo(responses);
            return responses;
        }
        throw new AccessDeniedException(ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.VIEW,
                ExceptionConstant.RESOURCES.FILE));
    }

    private List<FileMetadataResponse> handleDirectoryRequest(long directoryId, String keycloakUserId) {
        List<Long> fileIds = storageGrpcClient.getFileIdsOfDirectory(keycloakUserId, directoryId,
                Permission.VIEW.name());
        if (fileIds.isEmpty())
            return Collections.emptyList();
        List<FileMetadataResponse> responses = fileMetadataMapper
                .fileMetadataToFileMetadataResponse(fileMetadataRepository.getFileMetadataByFileIds(fileIds));
        enrichWithUserInfo(responses);
        return responses;
    }

    @SuppressWarnings("unchecked")
    private void enrichWithUserInfo(List<FileMetadataResponse> responses) {
        Set<String> userIds = responses.stream()
                .filter(r -> r.getMetadata() != null && r.getMetadata().containsKey("faces"))
                .flatMap(r -> ((List<Map<String, Object>>) r.getMetadata().get("faces")).stream())
                .map(face -> (String) face.get("userId"))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (userIds.isEmpty())
            return;

        Map<String, UserInfo> userInfoMap = userGrpcClient.getUsersInfo(new ArrayList<>(userIds));

        responses.forEach(r -> {
            if (r.getMetadata() != null && r.getMetadata().containsKey("faces")) {
                List<Map<String, Object>> faces = (List<Map<String, Object>>) r.getMetadata().get("faces");
                faces.forEach(face -> {
                    String uid = (String) face.get("userId");
                    if (uid != null && userInfoMap.containsKey(uid)) {
                        UserInfo info = userInfoMap.get(uid);
                        Map<String, Object> uInfoMap = new HashMap<>();
                        uInfoMap.put("username", info.getUsername());
                        uInfoMap.put("firstName", info.getFirstName());
                        uInfoMap.put("lastName", info.getLastName());
                        uInfoMap.put("email", info.getEmail());
                        face.put("user", uInfoMap);
                    }
                });
            }
        });
    }

}
