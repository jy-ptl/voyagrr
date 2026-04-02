package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.constant.ExceptionConstant;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.dto.*;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.model.Group;
import com.voyagrr.storageservice.model.Permission;
import com.voyagrr.storageservice.repository.*;
import com.voyagrr.storageservice.model.MediaShare;
import com.voyagrr.storageservice.service.MediaShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.voyagrr.common.constant.ExceptionConstant.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaShareServiceImpl implements MediaShareService {

    private final MediaShareRepository mediaShareRepository;
    private final PermissionRepository permissionRepository;
    private final GroupRepository groupRepository;
    private final DirectoryRepository directoryRepository;
    private final FileRepository fileRepository;

    @Override
    public String updateDirectoryPermission(DirectoryPermissionRequest request, String keycloakUserId) {

        Permission permission = permissionRepository
                .findByName(request.permission()).orElseThrow(
                        () -> new IllegalArgumentException(ENTITY_DOES_NOT_EXISTS.formatted(request.permission())));

        List<Long> ancestorsIncludingSelf = directoryRepository
                .getAllAncestorsIncludingSelf(request.directoryId()).stream().map(ele -> ele.id()).toList();

        boolean allowed = hasPermissionForDirectories(keycloakUserId, ancestorsIncludingSelf,
                com.voyagrr.common.enumeration.Permission.SHARE.name());

        if (!allowed) {
            throw new AccessDeniedException(ACCESS_DENIED_FOR_RESOURCE
                    .formatted(com.voyagrr.common.enumeration.Permission.SHARE.name(), RESOURCES.DIRECTORY));
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
    public ContentAccess contentAccessOfDirectoryByDirectoryIdAndUserId(List<Long> ancestorsIncludingSelf,
            List<Long> directoryIds, List<Long> fileIds, String keycloakUserId) {

        List<Object[]> rootDirRows = mediaShareRepository.findDirectoryPermissions(ancestorsIncludingSelf,
                keycloakUserId);

        List<Object[]> dirRows = mediaShareRepository.findDirectoryPermissions(directoryIds, keycloakUserId);
        Map<Long, List<String>> dirPermissions = groupPermissions(dirRows);

        List<Object[]> fileRows = mediaShareRepository.findFilePermissions(fileIds, keycloakUserId);
        Map<Long, List<String>> filePermissions = groupPermissions(fileRows);

        List<DirectoryAccess> dirResponses = dirPermissions.entrySet().stream()
                .map(entry -> DirectoryAccess.builder()
                        .directoryId(entry.getKey())
                        .permissions(entry.getValue())
                        .build())
                .toList();

        List<FileAccess> fileResponses = filePermissions.entrySet().stream()
                .map(entry -> FileAccess.builder()
                        .fileId(entry.getKey())
                        .permissions(entry.getValue())
                        .build())
                .toList();

        return ContentAccess.builder()
                .directories(dirResponses)
                .files(fileResponses)
                .rootDirectoryPermissions(
                        rootDirRows == null
                                ? Collections.emptyList()
                                : rootDirRows.stream()
                                        .map(row -> (String) row[1])
                                        .distinct()
                                        .toList())
                .build();

    }

    @Override
    public boolean hasPermissionForDirectories(String keycloakUserId, List<Long> directoryIds, String permission) {
        return mediaShareRepository.existsByUserIdAndDirectoryIdInAndPermission(keycloakUserId, directoryIds,
                permission);
    }

    @Override
    public boolean hasPermissionForFile(String keycloakUserId, long fileId, String permission) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.FILE)));

        boolean hasPermissionForAnyDir = hasPermissionForDirectories(keycloakUserId,
                directoryRepository.getAllAncestorsIncludingSelf(file.getDirectory().getId()).stream()
                        .mapToLong(DirectoryFlatResponse::id).boxed().toList(),
                permission);

        if (hasPermissionForAnyDir)
            return true;

        return hasPermissionForFileByUserId(keycloakUserId, fileId, permission);
    }

    @Override
    public String updateFilePermission(FilePermissionRequest request, String keycloakUserId) {
        Permission permission = permissionRepository
                .findByName(request.permission()).orElseThrow(
                        () -> new IllegalArgumentException(ENTITY_DOES_NOT_EXISTS.formatted(request.permission())));

        boolean hasFilePermission = hasPermissionForFile(keycloakUserId, request.fileId(),
                com.voyagrr.common.enumeration.Permission.SHARE.name());

        List<Long> ancestorsIncludingSelf = directoryRepository
                .getAllAncestorsIncludingSelf(fileRepository.getReferenceById(request.fileId()).getDirectory().getId())
                .stream().map(ele -> ele.id()).toList();

        boolean permissionForDirectories = hasPermissionForDirectories(keycloakUserId, ancestorsIncludingSelf,
                com.voyagrr.common.enumeration.Permission.SHARE.name());

        if (hasFilePermission || permissionForDirectories) {
            if (request.toGroupId() == null && request.toUserId() == null) {
                throw new IllegalArgumentException("Either toUserId or toGroupId must be provided");
            }
            if (request.toGroupId() != null && request.toUserId() != null) {
                throw new IllegalArgumentException("Cannot specify both toUserId and toGroupId");
            }
            if (request.toGroupId() == null) {
                mediaShareRepository.save(MediaShare
                        .builder()
                        .fileId(request.fileId())
                        .permissions(List.of(permission))
                        .userId(request.toUserId())
                        .build());

            } else {
                Group group = groupRepository.findById(request.toGroupId())
                        .orElseThrow(
                                () -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.GROUP)));

                mediaShareRepository.save(MediaShare.builder()
                        .fileId(request.fileId())
                        .permissions(List.of(permission))
                        .group(group)
                        .build());
            }
            return "Success";

        } else {
            throw new AccessDeniedException(ACCESS_DENIED_FOR_RESOURCE
                    .formatted(com.voyagrr.common.enumeration.Permission.SHARE.name(), RESOURCES.FILE));
        }
    }

    public boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission) {
        return mediaShareRepository.hasPermission(directoryId, keycloakUserId, permission);
    }

    @Override
    public List<Long> getFileIdsOfDirectory(String keycloakUserId, long direcotryId, String permission) {
        if (!mediaShareRepository.hasPermission(direcotryId, keycloakUserId, permission))
            throw new AccessDeniedException(ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE.formatted(permission,
                    ExceptionConstant.RESOURCES.DIRECTORY));
        return fileRepository
                .findByDirectory(directoryRepository.findById(direcotryId)
                        .orElseThrow(() -> new EntityNotFoundException(ExceptionConstant.ENTITY_DOES_NOT_EXISTS
                                .formatted(ExceptionConstant.RESOURCES.DIRECTORY))))
                .stream().map(file -> file.getId()).toList();
    }

    private boolean hasPermissionForFileByUserId(String keycloakUserId, long fileId, String permission) {
        return mediaShareRepository.existsByUserIdAndFileIdAndPermission(keycloakUserId, fileId, permission);
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
