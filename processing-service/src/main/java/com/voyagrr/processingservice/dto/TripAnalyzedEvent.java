package com.voyagrr.processingservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripAnalyzedEvent {

    private Long tripId;
    private String imagePath;
    private List<FaceResult> faces;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FaceResult {
        private String userId;
        private Double confidence;
        private BoundingBox bbox;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoundingBox {
        private Integer x1;
        private Integer y1;
        private Integer x2;
        private Integer y2;
    }

}
