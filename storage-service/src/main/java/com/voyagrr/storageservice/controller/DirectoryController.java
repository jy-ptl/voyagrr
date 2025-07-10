package com.voyagrr.storageservice.controller;

import com.voyagrr.storageservice.dto.DirectoryCreateRequest;
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

    @PostMapping("")
    public ResponseEntity<Long> create(@RequestBody DirectoryCreateRequest request,
                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.create(request, jwt.getSubject()));
    }

    @GetMapping("")
    public ResponseEntity<List<DirectoryTreeResponse>> getAllDirectoriesOfUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(directoryService.getAllDirectoriesOfUser(jwt.getSubject()));
    }

    @DeleteMapping("/{directoryId}")
    public ResponseEntity<String> deleteDirectoryById(@PathVariable(name = "directoryId") Long directoryId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(directoryService.deleteDirectoryById(directoryId, jwt.getSubject()));
    }

}
