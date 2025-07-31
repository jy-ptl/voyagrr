package com.voyagrr.storageservice.controller;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
import com.voyagrr.storageservice.dto.DirectoryContentResponse;
import com.voyagrr.storageservice.dto.DirectoryTreeResponse;
import com.voyagrr.storageservice.service.DirectoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryService directoryService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Long> create(@RequestBody DirectoryCreateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.create(request, jwt.getSubject()));
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<DirectoryTreeResponse>> getAllDirectoriesOfUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getAllDirectoriesOfUser(jwt.getSubject()));
    }

    @RequestMapping(value = "{directoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteDirectoryById(@PathVariable(name = "directoryId") Long directoryId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(directoryService.deleteDirectoryById(directoryId, jwt.getSubject()));
    }

    @RequestMapping(value = "{directoryId}", method = RequestMethod.GET)
    public ResponseEntity<DirectoryContentResponse> getDirectoryContents(
            @PathVariable(name = "directoryId") Long directoryId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getDirectoryContents(directoryId, jwt.getSubject()));
    }

}
