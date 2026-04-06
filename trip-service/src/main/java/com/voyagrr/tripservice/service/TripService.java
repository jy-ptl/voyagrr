package com.voyagrr.tripservice.service;

import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;

public interface TripService {
    TripCreateResponse createTrip(TripCreateRequest request, String keycloakUserId);
}
