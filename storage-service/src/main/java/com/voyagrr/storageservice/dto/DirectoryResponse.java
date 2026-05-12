package com.voyagrr.storageservice.dto;

import java.util.List;

public record DirectoryResponse(
        Long id,
        String name,
        short type,
        List<String> permission) {
}
