package com.voyagrr.storageservice.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentAccess {

    private List<String> rootDirectoryPermissions;
    private List<FileAccess> files;
    private List<DirectoryAccess> directories;

}
