package com.voyagrr.storageservice.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectoryAccess {

    private long directoryId;
    private List<String> permissions;

}
