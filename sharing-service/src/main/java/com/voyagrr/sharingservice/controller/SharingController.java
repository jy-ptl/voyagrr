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

@Slf4j
@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class SharingController {

    private final MediaShareService mediaShareService;

    @RequestMapping(value = "directory", method = RequestMethod.POST)
    public ResponseEntity<String> updateDirectoryPermission(@RequestBody DirectoryPermissionRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(mediaShareService.updateDirectoryPermission(request, jwt.getSubject()));
    }

    @RequestMapping(value = "file", method = RequestMethod.POST)
    public ResponseEntity<String> updateFilePermission(@RequestBody FilePermissionRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(mediaShareService.updateFilePermission(request, jwt.getSubject()));
    }

}
