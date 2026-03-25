package com.voyagrr.storageservice.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileAccess {

    private long fileId;
    private List<String> permissions;

}
