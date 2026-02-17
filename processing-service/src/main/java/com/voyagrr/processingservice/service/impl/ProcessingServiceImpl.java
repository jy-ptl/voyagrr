package com.voyagrr.processingservice.service.impl;

import java.io.InputStream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.voyagrr.processingservice.dto.ProcessRequest;
import com.voyagrr.processingservice.repository.FileMetadataRepository;
import com.voyagrr.processingservice.service.ProcessingService;
import com.voyagrr.processingservice.service.grpc.StorageGrpcClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessingServiceImpl implements ProcessingService {

    @Value("${minio.bucket}")
    private String bucket;

    private final MinioClient minioClient;
    private final FileMetadataRepository fileMetadataRepository;

    private final StorageGrpcClient storageGrpcClient;

    @Async
    @Override
    public void extractMetadata(ProcessRequest processRequest, String keycloakUserId) {

        String minioObjectKey = storageGrpcClient.getMinioObjectKeyFromFileId(processRequest.fileId(),
                keycloakUserId);

        if (minioObjectKey.equals(""))
            return;

        try {
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(processRequest.minioObjectKey())
                    .build());
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    System.out.println(tag);
                }
            }

        } catch (Exception exception) {
            log.error(exception.getMessage());
        }

    }

}
