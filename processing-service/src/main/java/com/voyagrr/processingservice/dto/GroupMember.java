package com.voyagrr.processingservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMember {
    private String keycloakUserId;
    private String sampleDirectory;
}
