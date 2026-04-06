package com.voyagrr.tripservice.service.impl;

import com.voyagrr.common.enumeration.TripVisibility;
import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;
import com.voyagrr.tripservice.model.Trip;
import com.voyagrr.tripservice.repository.TripRepository;
import com.voyagrr.tripservice.service.TripService;
import com.voyagrr.tripservice.service.grpc.client.StorageGrpcClient;
import com.voyagrr.tripservice.utility.TripMapper;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    private final StorageGrpcClient storageGrpcClient;

    @Override
    public TripCreateResponse createTrip(TripCreateRequest request, String keycloakUserId) {
        Trip trip = tripMapper.TripCreateRequestToTrip(request);
        long directoryId = storageGrpcClient.createDirectoryForTrip(keycloakUserId, request.title());
        long groupId = 0L;
        if (request.visibility() == TripVisibility.SHARED)
            groupId = storageGrpcClient.createOrValidateGroupForTrip(request.groupId() == null ? 0L : request.groupId(),
                    request.keycloakUserIds(),
                    keycloakUserId);

        trip.setDirectoryId(directoryId);
        trip.setGroupId(groupId);
        trip.setOwnerId(keycloakUserId);
        return tripMapper.TripToTripCreateResponse(tripRepository.save(trip));
    }

}
