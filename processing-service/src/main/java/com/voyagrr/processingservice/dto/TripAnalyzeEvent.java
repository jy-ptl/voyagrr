package com.voyagrr.processingservice.dto;

import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripAnalyzeEvent {
    private Long tripId;
    private String tripDirectory;
    private List<GroupMember> groupMembers;
}
