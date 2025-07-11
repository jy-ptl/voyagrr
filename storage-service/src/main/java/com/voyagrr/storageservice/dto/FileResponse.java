package com.voyagrr.storageservice.dto;

import java.util.List;

public record FileResponse(
        Long id,
        String name,
        String mineType,
        List<String> permissions
) {
}
