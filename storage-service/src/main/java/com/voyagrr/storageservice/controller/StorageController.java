package com.voyagrr.storageservice.controller;

import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.service.FileService;
import com.voyagrr.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Tag(name = "Storage", description = "APIs for accessing storage.")
@SecurityRequirement(name = "bearerAuth")
public class StorageController {

    private final StorageService storageService;
    private final FileService fileService;

    @Operation(summary = "Upload a file", description = "Upload a file to a specified directory")
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("directoryId") long directoryId,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        FileUploadRequest request = new FileUploadRequest(name, directoryId);
        return ResponseEntity.ok().body(storageService.upload(request, file, keycloakUserId));
    }

    @Operation(summary = "Upload multiple files", description = "Upload multiple files to a specified directory")
    @RequestMapping(value = "upload/batch", method = RequestMethod.POST)
    public ResponseEntity<String> uploadBatch(@RequestParam("files") List<MultipartFile> files,
            @RequestParam("directoryId") long directoryId,
            @AuthenticationPrincipal Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        return ResponseEntity.ok().body(storageService.uploadBatch(directoryId, files, keycloakUserId));
    }

    @Operation(summary = "Download a file", description = "Download a file by fileId")
    @RequestMapping(value = "{fileId}", method = RequestMethod.GET, produces = APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> download(@PathVariable(name = "fileId") long fileId,
            @AuthenticationPrincipal Jwt jwt) {

        File file = fileService.findById(fileId);
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.valueOf(file.getMimeType()))
                .body(storageService.download(fileId, jwt.getSubject()));
    }

    @Operation(summary = "Delete a file", description = "Delete a file by fileId")
    @RequestMapping(value = "{fileId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable(name = "fileId") long fileId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(storageService.deleteFile(fileId, jwt.getSubject()));
    }

    @Operation(summary = "Download a file thumbnail", description = "Download a file thumbnail by fileId")
    @RequestMapping(value = "{fileId}/thumbnail", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> downloadThumbnail(@PathVariable(name = "fileId") long fileId,
            @AuthenticationPrincipal Jwt jwt) {
        Resource resource = storageService.downloadThumbnail(fileId, jwt.getSubject());
        if (resource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }

}
