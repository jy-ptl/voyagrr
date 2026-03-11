package com.voyagrr.sharingservice.controller;

import com.voyagrr.sharingservice.dto.DirectoryPermissionRequest;
import com.voyagrr.sharingservice.dto.FilePermissionRequest;
import com.voyagrr.sharingservice.service.MediaShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Slf4j
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
@Tag(name = "Share", description = "APIs related to sharing a resource")
@SecurityRequirement(name = "bearerAuth")
public class SharingController {

    private final MediaShareService mediaShareService;

    @Operation(summary = "Share a directory", description = "Share a directory with specified permission to a user or a group")
    @RequestMapping(value = "directory", method = RequestMethod.POST)
    public ResponseEntity<String> updateDirectoryPermission(@RequestBody DirectoryPermissionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(mediaShareService.updateDirectoryPermission(request, jwt.getSubject()));
    }

    @Operation(summary = "Share a file", description = "Share a file with specified permission to a user or a group")
    @RequestMapping(value = "file", method = RequestMethod.POST)
    public ResponseEntity<String> updateFilePermission(@RequestBody FilePermissionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(mediaShareService.updateFilePermission(request, jwt.getSubject()));
    }

}
