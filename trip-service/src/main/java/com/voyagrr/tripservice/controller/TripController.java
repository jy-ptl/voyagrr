package com.voyagrr.tripservice.controller;

import com.voyagrr.tripservice.dto.TripCreateRequest;
import com.voyagrr.tripservice.dto.TripCreateResponse;
import com.voyagrr.tripservice.service.TripService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/trip")
@RequiredArgsConstructor
@Tag(name = "Trip", description = "APIs related to trips")
@SecurityRequirement(name = "bearerAuth")
public class TripController {

    private final TripService tripService;

    @Operation(summary = "Create a trip", description = "Creating a trip")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<TripCreateResponse> createTrip(@Valid @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(tripService.createTrip(request, jwt.getSubject()));
    }

}
