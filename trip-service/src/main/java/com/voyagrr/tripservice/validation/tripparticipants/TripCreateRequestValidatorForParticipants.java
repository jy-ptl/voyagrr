package com.voyagrr.tripservice.validation.tripparticipants;

import com.voyagrr.common.enumeration.TripVisibility;
import com.voyagrr.tripservice.dto.TripCreateRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TripCreateRequestValidatorForParticipants
        implements ConstraintValidator<ValidTripParticipants, TripCreateRequest> {

    @Override
    public boolean isValid(TripCreateRequest request, ConstraintValidatorContext context) {

        if (request == null)
            return true;

        boolean hasGroupId = request.groupId() != null;
        boolean hasUsers = request.keycloakUserIds() != null && !request.keycloakUserIds().isEmpty();

        TripVisibility visibility = request.visibility();

        if (hasGroupId && hasUsers)
            return buildError(context, "Provide either group or users, not both");

        if (visibility == TripVisibility.PRIVATE)
            if (hasGroupId || hasUsers)
                return buildError(context, "For PRIVATE trips, group or users are not permitted");

        if (visibility == TripVisibility.SHARED)
            if (!hasGroupId && !hasUsers)
                return buildError(context, "For SHARED trips, provide group or users");

        return true;
    }

    private boolean buildError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("groupId")
                .addConstraintViolation();
        return false;
    }

}
