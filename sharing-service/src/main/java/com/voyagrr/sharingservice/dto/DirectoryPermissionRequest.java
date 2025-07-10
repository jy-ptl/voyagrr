package com.voyagrr.sharingservice.dto;

import com.voyagrr.common.enumeration.Permission;

public record DirectoryPermissionRequest(
        Long directoryId,
        String toUserId,
        Long toGroupId,
        Permission permission
) {
}
