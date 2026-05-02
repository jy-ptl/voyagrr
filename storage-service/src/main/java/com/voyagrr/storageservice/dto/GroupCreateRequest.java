package com.voyagrr.storageservice.dto;

import java.util.List;

public record GroupCreateRequest(
        String name,
        List<String> members) {
}
