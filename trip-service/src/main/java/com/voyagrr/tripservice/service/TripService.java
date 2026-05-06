package com.voyagrr.tripservice.service;

import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;
import com.voyagrr.tripservice.dto.TripResponse;

import java.util.List;

public interface TripService {
    TripCreateResponse createTrip(TripCreateRequest request, String keycloakUserId);

    String proccessTrip(long tripId, String keycloakUserId);

    List<TripResponse> getTripsForUser(String keycloakUserId);

    TripResponse getTripById(long tripId, String keycloakUserId);
}
