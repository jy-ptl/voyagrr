package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.enumeration.Permission;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.StorageService;
import com.voyagrr.storageservice.service.grpc.SharingPermissionGrpcClient;
import com.voyagrr.storageservice.utility.FileUtility;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final FileUtility fileUtility;
    private final MinioClient minioClient;

    private final SharingPermissionGrpcClient sharingPermissionGrpcClient;

    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void init() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            log.error("Error while initializing MinioStorageService", e.getMessage());
        }
    }

    @Override
    public String upload(FileUploadRequest request, MultipartFile file, String keycloakUserId) {

        String mimeType = fileUtility.getMimeType(file);

        Directory directory = directoryRepository.findById(request.directoryId())
                .orElseThrow(() -> new EntityNotFoundException("Directory with id : " + request.directoryId() + " does not exists."));

        String minioObjectKey = directoryRepository.buildMinioObjectPathFromDirectoryId(request.directoryId());

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uuidFilename = UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension : "");

        try (InputStream input = file.getInputStream()) {

            boolean allowed = sharingPermissionGrpcClient.hasPermission(
                    keycloakUserId, request.directoryId(), Permission.UPLOAD.name());

            if (!allowed)
                throw new AccessDeniedException("User does not have upload permission to this directory");

            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(minioObjectKey + "/" + uuidFilename)
                            .stream(input, file.getSize(), -1)
                            .contentType(mimeType)
                            .build()
            );

            fileRepository
                    .save(File
                            .builder()
                            .name(file.getOriginalFilename())
                            .directory(directory)
                            .minioObjectKey(minioObjectKey + "/" + uuidFilename)
                            .mimeType(mimeType)
                            .ownerId(keycloakUserId)
                            .build());

            return "Success";
        } catch (Exception e) {
            log.error("Upload failed", e);
        }
        return "";
    }

    @Override
    public void deleteFiles(List<File> files) {

        List<DeleteObject> minioObjects = files
                .stream()
                .filter(file -> file.getMinioObjectKey() != null)
                .map(file -> new DeleteObject(file.getMinioObjectKey()))
                .toList();

        if (!minioObjects.isEmpty()) {
            try {
                minioClient.removeObjects(RemoveObjectsArgs.builder()
                        .bucket(bucket)
                        .objects(minioObjects)
                        .build()).forEach(deleteErrorResult -> {
                    try {
                        deleteErrorResult.get();
                    } catch (Exception exception) {
                        try {
                            DeleteError error = deleteErrorResult.get();
                            log.error(error.bucketName());
                        } catch (Exception nested) {
                            log.error(nested.getMessage());
                        }
                    }
                });
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        }
    }
}
