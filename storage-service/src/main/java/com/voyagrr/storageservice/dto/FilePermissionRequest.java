package com.voyagrr.storageservice.dto;

import com.voyagrr.common.enumeration.Permission;

public record FilePermissionRequest(
        Long fileId,
        String toUserId,
        Long toGroupId,
        Permission permission) {
}
