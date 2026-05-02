package com.voyagrr.storageservice.dto;

import java.util.List;

public record GroupUpdateRequest(
        String name,
        List<String> members) {
}
