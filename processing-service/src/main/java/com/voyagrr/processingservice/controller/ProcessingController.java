package com.voyagrr.processingservice.controller;

import com.voyagrr.processingservice.dto.ProcessRequest;
import com.voyagrr.processingservice.service.ProcessingService;

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

@RestController
@RequestMapping(value = "/api/process")
@RequiredArgsConstructor
@Tag(name = "Process", description = "APIs for managing metadata for uploaded files")
@SecurityRequirement(name = "bearerAuth")
public class ProcessingController {

    private final ProcessingService processingService;

    @Operation(summary = "Process metadata", description = "Process metadata of a file")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<String> process(@Valid @RequestBody ProcessRequest processRequest,
            @AuthenticationPrincipal Jwt jwt) {
        processingService.extractMetadata(processRequest, jwt.getSubject());
        return ResponseEntity.ok().body("In process");

    }

}
