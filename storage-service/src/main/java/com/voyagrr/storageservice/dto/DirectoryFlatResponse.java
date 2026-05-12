package com.voyagrr.storageservice.dto;

public record DirectoryFlatResponse(
        Long id,
        String name,
        Long parentDirectoryId,
        short type) {
}
