package com.voyagrr.tripservice.service.impl;

import static com.voyagrr.common.constant.ExceptionConstant.ACCESS_DENIED_FOR_RESOURCE;
import static com.voyagrr.common.constant.ExceptionConstant.ENTITY_DOES_NOT_EXISTS;
import java.util.List;

import com.voyagrr.common.constant.ExceptionConstant.RESOURCES;
import com.voyagrr.common.enumeration.TripVisibility;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;
import com.voyagrr.tripservice.dto.TripResponse;
import com.voyagrr.tripservice.model.Trip;
import com.voyagrr.tripservice.repository.TripRepository;
import com.voyagrr.tripservice.service.TripService;
import com.voyagrr.tripservice.service.grpc.client.ProcessingGrpcClient;
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
    private final ProcessingGrpcClient processingGrpcClient;

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

        if (groupId > 0)
            storageGrpcClient.addMediaShare(directoryId, groupId);

        return tripMapper.TripToTripCreateResponse(tripRepository.save(trip));
    }

    @Override
    public String proccessTrip(long tripId, String keycloakUserId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.TRIP)));
        if (trip.getOwnerId().equals(keycloakUserId)) {
            processingGrpcClient.processTrip(tripId, trip
                    .getDirectoryId(), trip.getGroupId(), keycloakUserId);
            return "success";
        }
        throw new AccessDeniedException(
                ACCESS_DENIED_FOR_RESOURCE.formatted(RESOURCES.TRIP, "PROCESS"));
    }

    @Override
    public List<TripResponse> getTripsForUser(String keycloakUserId) {
        List<Long> groupIds = storageGrpcClient.getGroupIdsForUser(keycloakUserId);
        if (groupIds.isEmpty()) {
            groupIds = List.of(-1L); // Use a non-existent group ID to avoid issues with IN clause if needed, 
            // though most DBs handle empty IN differently. 
            // Actually, let's just use an empty list if supported, but some versions of Spring Data JPA might struggle.
            // Better to handle it explicitly or use a more robust query.
        }
        return tripRepository.findAllByOwnerIdOrGroupIdInAndDeletedFalse(keycloakUserId, groupIds).stream()
                .map(tripMapper::tripToTripResponse)
                .toList();
    }

    @Override
    public TripResponse getTripById(long tripId, String keycloakUserId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(
                () -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.TRIP)));

        if (trip.getOwnerId().equals(keycloakUserId)) {
            return tripMapper.tripToTripResponse(trip);
        }

        if (trip.getGroupId() > 0) {
            List<Long> groupIds = storageGrpcClient.getGroupIdsForUser(keycloakUserId);
            if (groupIds.contains(trip.getGroupId())) {
                return tripMapper.tripToTripResponse(trip);
            }
        }

        throw new AccessDeniedException(
                ACCESS_DENIED_FOR_RESOURCE.formatted(RESOURCES.TRIP, "VIEW"));
    }

}
