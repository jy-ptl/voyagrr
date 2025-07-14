package com.voyagrr.storageservice.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectoryContentResponse {
    List<String> permission = new ArrayList<>();
    List<FileResponse> files;
    List<DirectoryResponse> children;
}
