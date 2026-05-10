package com.voyagrr.storageservice.controller;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.dto.DirectoryContentResponse;
import com.voyagrr.storageservice.dto.DirectoryTreeResponse;
import com.voyagrr.storageservice.dto.FileThumbnailResponse;
import com.voyagrr.storageservice.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
@Tag(name = "Directory", description = "APIs for managing directories")
@SecurityRequirement(name = "bearerAuth")
public class DirectoryController {

    private final DirectoryService directoryService;

    @Operation(summary = "Create new directory", description = "Creates a directory for the authenticated user with permissions")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Long> create(@RequestBody DirectoryCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.create(request, jwt.getSubject()));
    }

    @Operation(summary = "Get all directories", description = "Retrieves all directories belonging to the authenticated user")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<DirectoryTreeResponse>> getAllDirectoriesOfUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getAllDirectoriesOfUser(jwt.getSubject()));
    }

    @Operation(summary = "Delete directory", description = "Deletes a directory by ID if it belongs to the authenticated user")
    @RequestMapping(value = "{directoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDirectoryById(@PathVariable(name = "directoryId") Long directoryId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(directoryService.deleteDirectoryById(directoryId, jwt.getSubject()));
    }

    @Operation(summary = "Get directory contents", description = "Retrieves the contents of a directory for the authenticated user")
    @RequestMapping(value = "{directoryId}", method = RequestMethod.GET)
    public ResponseEntity<DirectoryContentResponse> getDirectoryContents(
            @PathVariable(name = "directoryId") Long directoryId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getDirectoryContents(directoryId, jwt.getSubject()));
    }

    @Operation(summary = "Get directory thumbnails", description = "Retrieves the thumbnails for all files in a directory")
    @RequestMapping(value = "{directoryId}/thumbnails", method = RequestMethod.GET)
    public ResponseEntity<List<FileThumbnailResponse>> getThumbnailsForDirectory(
            @PathVariable(name = "directoryId") Long directoryId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getThumbnailsForDirectory(directoryId, jwt.getSubject()));
    }

}
