package com.voyagrr.metadataservice.controller;

import com.voyagrr.metadataservice.dto.MetadataProcessRequest;
import com.voyagrr.metadataservice.service.MetadataService;

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
@RequestMapping(value = "/api/metadata")
@RequiredArgsConstructor
@Tag(name = "Metadata", description = "APIs for managing metadata for uploaded files")
@SecurityRequirement(name = "bearerAuth")
public class MetadataController {

    private final MetadataService metadataService;

    @Operation(summary = "Process metadata", description = "Process metadata of a file")
    @RequestMapping(value = "process", method = RequestMethod.POST)
    public ResponseEntity<String> process(@Valid @RequestBody MetadataProcessRequest metadataProcessRequest,
            @AuthenticationPrincipal Jwt jwt) {
        metadataService.extractMetadata(metadataProcessRequest, jwt.getSubject());
        return ResponseEntity.ok().body("In process");

    }

}
