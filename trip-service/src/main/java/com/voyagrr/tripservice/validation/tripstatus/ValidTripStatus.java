package com.voyagrr.tripservice.validation.tripstatus;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TripCreateRequestValidatorForStatus.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTripStatus {

    String message() default "Invalid trip status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
