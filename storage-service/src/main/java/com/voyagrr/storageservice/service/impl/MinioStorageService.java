package com.voyagrr.storageservice.service.impl;

import com.voyagrr.common.enumeration.Permission;
import com.voyagrr.common.exception.AccessDeniedException;
import com.voyagrr.common.exception.EntityNotFoundException;
import com.voyagrr.storageservice.dto.DirectoryFlatResponse;
import com.voyagrr.storageservice.dto.FileUploadRequest;
import com.voyagrr.storageservice.model.Directory;
import com.voyagrr.storageservice.model.File;
import com.voyagrr.storageservice.repository.DirectoryRepository;
import com.voyagrr.storageservice.repository.FileRepository;
import com.voyagrr.storageservice.service.MediaShareService;
import com.voyagrr.storageservice.service.StorageService;
import com.voyagrr.storageservice.service.grpc.client.ProcessingGrpcClient;
import com.voyagrr.storageservice.utility.FileUtility;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static com.voyagrr.common.constant.ExceptionConstant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements StorageService {

    private final FileUtility fileUtility;
    private final MinioClient minioClient;

    private final MediaShareService mediaShareService;

    private final FileRepository fileRepository;
    private final DirectoryRepository directoryRepository;

    private final ProcessingGrpcClient processingGrpcClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.processed-video-bucket}")
    private String processedVideobucket;

    @PostConstruct
    public void init() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(processedVideobucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(processedVideobucket).build());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String upload(FileUploadRequest request, MultipartFile file, String keycloakUserId) {

        String mimeType = fileUtility.getMimeType(file);

        Directory directory = directoryRepository.findById(request.directoryId())
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.DIRECTORY)));

        String minioObjectKey = directoryRepository.buildMinioObjectPathFromDirectoryId(request.directoryId());

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String uuidFilename = UUID.randomUUID() + (StringUtils.hasText(extension) ? "." + extension : "");

        try (InputStream input = file.getInputStream()) {

            boolean allowed = mediaShareService.hasPermissionForDirectories(
                    keycloakUserId, directoryRepository.getAllAncestorsIncludingSelf(request.directoryId()).stream()
                            .mapToLong(DirectoryFlatResponse::id).boxed().toList(),
                    Permission.UPLOAD.name());

            if (!allowed)
                throw new AccessDeniedException(
                        ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.UPLOAD.name(), RESOURCES.DIRECTORY));

            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(minioObjectKey + "/" + uuidFilename)
                            .stream(input, file.getSize(), -1)
                            .contentType(mimeType)
                            .build());

            File savedFile = fileRepository
                    .save(File
                            .builder()
                            .name(file.getOriginalFilename())
                            .directory(directory)
                            .minioObjectKey(minioObjectKey + "/" + uuidFilename)
                            .mimeType(mimeType)
                            .ownerId(keycloakUserId)
                            .build());

            if (directory.getName().equals("Samples"))
                processingGrpcClient.startSampleImageEmbedding(savedFile.getOwnerId(), "dir_" + directory.getId());
            processingGrpcClient.startFileProcessing(savedFile.getId(), savedFile.getMinioObjectKey());

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

    @Override
    public Resource download(long fileId, String keycloakUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.FILE)));
        if (mediaShareService.hasPermissionForFile(keycloakUserId, fileId, Permission.DOWNLOAD.name())) {
            try {
                InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(file.getMinioObjectKey())
                        .build());
                return new InputStreamResource(inputStream);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        } else {
            throw new AccessDeniedException(
                    ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.DOWNLOAD.name(), RESOURCES.FILE));
        }
        return null;
    }

    @Override
    public String deleteFile(long fileId, String keycloakUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.FILE)));
        if (mediaShareService.hasPermissionForFile(keycloakUserId, fileId, Permission.DELETE.name())) {
            deleteFiles(List.of(file));
            fileRepository.delete(file);
            return "Success";
        } else {
            throw new AccessDeniedException(
                    ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.DELETE.name(), RESOURCES.FILE));
        }
    }

    @Override
    public String getMinioObjectKey(Long fileId, String keycloakUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.FILE)));
        if (mediaShareService.hasPermissionForFile(keycloakUserId, fileId, Permission.VIEW.name())) {
            return file.getMinioObjectKey();
        }
        return "";
    }

    @Override
    public Resource downloadThumbnail(long fileId, String keycloakUserId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_DOES_NOT_EXISTS.formatted(RESOURCES.FILE)));
        if (mediaShareService.hasPermissionForFile(keycloakUserId, fileId, Permission.VIEW.name())) {
            if (file.getThumbnailKey() == null) {
                return null;
            }
            try {
                InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(file.getThumbnailKey())
                        .build());
                return new InputStreamResource(inputStream);
            } catch (Exception exception) {
                log.error(exception.getMessage());
            }
        } else {
            throw new AccessDeniedException(
                    ACCESS_DENIED_FOR_RESOURCE.formatted(Permission.VIEW.name(), RESOURCES.FILE));
        }
        return null;
    }

}
