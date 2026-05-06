package com.voyagrr.tripservice.utility;

import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;
import com.voyagrr.tripservice.model.Trip;

import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public Trip TripCreateRequestToTrip(TripCreateRequest request) {
        return Trip.builder()
                .title(request.title())
                .description(request.description())
                .visibility(request.visibility())
                .status(request.status())
                .build();
    }

    public TripCreateResponse TripToTripCreateResponse(Trip trip) {
        return TripCreateResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .description(trip.getDescription())
                .visibility(trip.getVisibility().name())
                .status(trip.getStatus().name())
                .build();
    }

    public com.voyagrr.tripservice.dto.TripResponse tripToTripResponse(Trip trip) {
        return com.voyagrr.tripservice.dto.TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .description(trip.getDescription())
                .visibility(trip.getVisibility().name())
                .status(trip.getStatus().name())
                .ownerId(trip.getOwnerId())
                .build();
    }

}
