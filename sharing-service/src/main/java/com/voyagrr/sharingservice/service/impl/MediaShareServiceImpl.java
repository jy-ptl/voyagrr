package com.voyagrr.sharingservice.service.impl;

import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.common.proto.ContentAccessResponse;
import com.voyagrr.common.proto.DirectoryAccessResponse;
import com.voyagrr.common.proto.FileAccessResponse;
import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;
import com.voyagrr.sharingservice.model.Group;
import com.voyagrr.sharingservice.model.Permission;
import com.voyagrr.sharingservice.model.MediaShare;
import com.voyagrr.sharingservice.repository.GroupRepository;
import com.voyagrr.sharingservice.repository.MediaShareRepository;
import com.voyagrr.sharingservice.repository.PermissionRepository;
import com.voyagrr.sharingservice.service.MediaShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.voyagrr.common.constant.ExceptionConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaShareServiceImpl implements MediaShareService {

    private final MediaShareRepository mediaShareRepository;
    private final PermissionRepository permissionRepository;
    private final GroupRepository groupRepository;

    @Override
    public String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId) {

        Permission permission = permissionRepository
                .findByName(request.permission()).orElseThrow(() -> new IllegalArgumentException(ENTITY_DOES_NOT_EXISTS.formatted(request.permission())));

        boolean allowed = hasPermissionForDirectory(request.directoryId(), keycloakUserId, com.voyagrr.common.enumeration.Permission.SHARE.name());

        if (!allowed) {
            throw new AccessDeniedException(ACCESS_DENIED_FOR_RESOURCE.formatted(com.voyagrr.common.enumeration.Permission.SHARE.name(), RESOURCES.DIRECTORY));
        }

        if (request.toGroupId() == null && request.toUserId() == null) {
            throw new IllegalArgumentException("Either toUserId or toGroupId must be provided");
        }

        if (request.toGroupId() != null && request.toUserId() != null) {
            throw new IllegalArgumentException("Cannot specify both toUserId and toGroupId");
        }

        if (request.toGroupId() == null) {

            mediaShareRepository.save(MediaShare
                    .builder()
                    .directoryId(request.directoryId())
                    .permissions(List.of(permission))
                    .userId(request.toUserId())
                    .build());

        } else {

            Group group = groupRepository.findById(request.toGroupId())
                    .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.GROUP)));

            mediaShareRepository.save(MediaShare.builder()
                    .directoryId(request.directoryId())
                    .permissions(List.of(permission))
                    .group(group)
                    .build());
        }

        return "Success";
    }

    @Override
    public void createDefaultPermissions(Long directoryId, String keycloakUserId) {
        List<Permission> allPermissions = permissionRepository.findAll();
        mediaShareRepository.save(MediaShare.builder()
                .userId(keycloakUserId)
                .directoryId(directoryId)
                .permissions(allPermissions)
                .build());
    }

    @Override
    @Transactional
    public boolean deleteAllPermissionByDirectoryIds(List<Long> directoryIds) {
        deleteMediaShares(mediaShareRepository.findAllMediaSharesByDirectoryIds(directoryIds));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAllPermissionByUserIds(List<String> userIds) {
        deleteMediaShares(mediaShareRepository.findAllMediaSharesByUserIds(userIds));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAllPermissionByFileIds(List<Long> fileIds) {
        deleteMediaShares(mediaShareRepository.findAllMediaSharesByFileIds(fileIds));
        return true;
    }

    @Override
    @Transactional
    public boolean deleteAllPermissionByGroupIds(List<Long> groupIds) {
        deleteMediaShares(mediaShareRepository.findAllMediaSharesByGroupIds(groupIds));
        return true;
    }

    @Override
    public ContentAccessResponse contentAccessOfDirectoryByDirectoryIdAndUserId(Long directoryId, List<Long> directoryIds, List<Long> fileIds, String keycloakUserId) {

        List<Object[]> rootDirRows = mediaShareRepository.findDirectoryPermissions(List.of(directoryId), keycloakUserId);
        Map<Long, List<String>> rootDirPermissions = groupPermissions(rootDirRows);

        List<Object[]> dirRows = mediaShareRepository.findDirectoryPermissions(directoryIds, keycloakUserId);
        Map<Long, List<String>> dirPermissions = groupPermissions(dirRows);

        List<Object[]> fileRows = mediaShareRepository.findFilePermissions(fileIds, keycloakUserId);
        Map<Long, List<String>> filePermissions = groupPermissions(fileRows);

        List<DirectoryAccessResponse> dirResponses = dirPermissions.entrySet().stream()
                .map(entry -> DirectoryAccessResponse.newBuilder()
                        .setDirectoryId(entry.getKey())
                        .addAllPermission(entry.getValue())
                        .build())
                .toList();

        List<FileAccessResponse> fileResponses = filePermissions.entrySet().stream()
                .map(entry -> FileAccessResponse.newBuilder()
                        .setFileId(entry.getKey())
                        .addAllPermission(entry.getValue())
                        .build())
                .toList();

        ContentAccessResponse.Builder builder = ContentAccessResponse.newBuilder()
                .addAllDirectories(dirResponses)
                .addAllFiles(fileResponses);

        if (rootDirPermissions.containsKey(directoryId)) {
            builder.setRootDirectory(DirectoryAccessResponse.newBuilder()
                    .setDirectoryId(directoryId)
                    .addAllPermission(rootDirPermissions.get(directoryId))
                    .build());
        }

        return builder.build();
    }

    public boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission) {
        return mediaShareRepository.hasPermission(directoryId, keycloakUserId, permission);
    }

    private void deleteMediaShares(List<MediaShare> mediaShares) {
        for (MediaShare mediaShare : mediaShares) {
            mediaShare.getPermissions().clear();
            mediaShareRepository.save(mediaShare);
            mediaShareRepository.delete(mediaShare);
        }
    }

    private Map<Long, List<String>> groupPermissions(List<Object[]> rows) {
        Map<Long, List<String>> permissionMap = new HashMap<>();
        for (Object[] row : rows) {
            Long id = ((Number) row[0]).longValue();
            String permission = (String) row[1];
            permissionMap.computeIfAbsent(id, k -> new ArrayList<>()).add(permission);
        }
        return permissionMap;
    }


}
