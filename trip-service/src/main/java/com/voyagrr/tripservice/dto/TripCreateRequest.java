package com.voyagrr.tripservice.dto;

import java.util.List;

import com.voyagrr.common.enumeration.TripStatus;
import com.voyagrr.common.enumeration.TripVisibility;
import com.voyagrr.tripservice.validation.tripparticipants.ValidTripParticipants;
import com.voyagrr.tripservice.validation.tripstatus.ValidTripStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@ValidTripParticipants
@ValidTripStatus
public record TripCreateRequest(
        @NotBlank(message = "title is required") @Size(min = 3, max = 50, message = "title must be between 3 and 50 characters") String title,
        @Size(max = 500, message = "Description cannot exceed 500 characters") String description,
        @NotNull(message = "Visibility is required") TripVisibility visibility,
        @NotNull(message = "Status is required") TripStatus status,
        Long groupId,
        List<String> keycloakUserIds) {
}
