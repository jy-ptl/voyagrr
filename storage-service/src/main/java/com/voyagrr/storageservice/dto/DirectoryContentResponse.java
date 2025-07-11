package com.voyagrr.storageservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryContentResponse {
    List<FileResponse> files;
    List<DirectoryResponse> children;
}
