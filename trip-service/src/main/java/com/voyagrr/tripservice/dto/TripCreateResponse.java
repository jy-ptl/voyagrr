package com.voyagrr.tripservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TripCreateResponse {

    private Long id;
    private String title;
    private String description;
    private String visibility;
    private String status;

}
