package com.voyagrr.storageservice.controller;


import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam("name") String name,
                                         @RequestParam("directoryId") long directoryId,
                                         @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        FileUploadRequest request = new FileUploadRequest(name, directoryId);
        return ResponseEntity.ok().body(storageService.upload(request, file, keycloakUserId));
    }


}
