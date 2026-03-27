package com.voyagrr.processingservice.controller;

import java.util.List;

import com.voyagrr.processingservice.dto.FileMetadataRequest;
import com.voyagrr.processingservice.dto.FileMetadataResponse;
import com.voyagrr.processingservice.service.FileMetadataService;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
@Tag(name = "Metadata", description = "APIs for file metadata")
@SecurityRequirement(name = "bearerAuth")
public class FileMetadataController {

    private final FileMetadataService fileMetadataService;

    @Operation(summary = "Get file metadata", description = "Get file metadata of either a file or all file in a directory")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<List<FileMetadataResponse>> getFileMetadata(@RequestBody FileMetadataRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(fileMetadataService.getFileMetadata(request, jwt.getSubject()));
    }

}
