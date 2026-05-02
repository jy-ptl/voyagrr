package com.voyagrr.storageservice.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record GroupResponse(
        Long groupId,
        String name,
        String ownerId,
        List<String> members) {
}
