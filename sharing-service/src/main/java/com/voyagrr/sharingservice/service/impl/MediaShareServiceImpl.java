package com.voyagrr.sharingservice.service.impl;

import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;
import com.voyagrr.sharingservice.model.Group;
import com.voyagrr.sharingservice.model.Permission;
import com.voyagrr.sharingservice.model.MediaShare;
import com.voyagrr.sharingservice.repository.GroupRepository;
import com.voyagrr.sharingservice.repository.MediaShareRepository;
import com.voyagrr.sharingservice.repository.PermissionRepository;
import com.voyagrr.sharingservice.service.MediaShareService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .findByName(request.permission()).orElseThrow(() -> new IllegalArgumentException("Invalid permission"));

        boolean allowed = hasPermissionForDirectory(request.directoryId(), keycloakUserId, com.voyagrr.common.enumeration.Permission.SHARE.name());

        if (!allowed) {
            throw new AccessDeniedException("You don't have permission to share this directory");
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
                    .orElseThrow(() -> new EntityNotFoundException("Group not found"));

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

    public boolean hasPermissionForDirectory(Long directoryId, String keycloakUserId, String permission) {
        return mediaShareRepository.hasPermission(directoryId, keycloakUserId, permission);
    }

}
