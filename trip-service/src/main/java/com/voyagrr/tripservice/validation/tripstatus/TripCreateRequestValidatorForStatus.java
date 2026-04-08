package com.voyagrr.tripservice.validation.tripstatus;

import com.voyagrr.common.enumeration.TripStatus;
import com.voyagrr.tripservice.dto.TripCreateRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TripCreateRequestValidatorForStatus implements ConstraintValidator<ValidTripStatus, TripCreateRequest> {

    @Override
    public boolean isValid(TripCreateRequest request, ConstraintValidatorContext context) {

        if (request == null)
            return true;

        TripStatus status = request.status();

        if (status == TripStatus.COMPLETED)
            return buildError(context, "status can not be COMPLETED while creating new trip");

        return true;
    }

    private boolean buildError(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("status")
                .addConstraintViolation();
        return false;
    }

}
