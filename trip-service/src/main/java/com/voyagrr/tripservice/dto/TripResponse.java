package com.voyagrr.tripservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private Long id;
    private String title;
    private String description;
    private String visibility;
    private String status;
    private String ownerId;

}
